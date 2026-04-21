package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.CreatePurchaseCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.DeletePurchaseCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.UpdateProductCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.ProductUpdatedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.PurchaseCreatedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.PurchaseDeletedReply;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central orchestrator for the Saga pattern -- coordinates the distributed purchase
 * transaction that spans Neo4j (GraphDatabaseService) and Elasticsearch (ElasticSearchDatabaseService).
 *
 * Orchestration flow:
 *   startSaga()
 *     -> sends CreatePurchaseCommand -> Neo4j CommandListener creates the PURCHASED relationship
 *     -> receives PurchaseCreatedReply
 *   handlePurchaseCreatedReply() [success]
 *     -> sends UpdateProductCommand -> ES CommandListener increments purchaseCount
 *     -> receives ProductUpdatedReply
 *   handleProductUpdatedReply() [success]
 *     -> saga state = COMPLETED
 *   handleProductUpdatedReply() [failure]
 *     -> sends DeletePurchaseCommand -> Neo4j removes the relationship (compensation)
 *     -> receives PurchaseDeletedReply
 *   handlePurchaseDeletedReply()
 *     -> saga state = COMPENSATED / FAILED
 *
 * Active saga instances are stored in an in-memory ConcurrentHashMap (thread-safe).
 * For production use, replace with a durable store such as Redis or a relational database.
 */
@Slf4j
@Component
public class SagaOrchestrator {

    /** In-memory registry of active saga instances keyed by sagaId. */
    private final ConcurrentHashMap<String, SagaInstance> sagaRegistry = new ConcurrentHashMap<>();

    private final RabbitTemplate rabbitTemplate;

    public SagaOrchestrator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // =========================================================================
    // Saga startup
    // =========================================================================

    /**
     * Creates a new SagaInstance and sends CreatePurchaseCommand to the Neo4j service.
     * This is the entry point of the orchestration flow, called from the REST controller.
     *
     * @param customerId    ID of the customer making the purchase
     * @param productId     ID of the product being purchased
     * @param numberOfItems number of items in the order
     * @return sagaId of the newly created saga instance
     */
    public String startSaga(Long customerId, String productId, Integer numberOfItems) {
        String sagaId = UUID.randomUUID().toString();
        SagaInstance instance = new SagaInstance(sagaId, customerId, productId, numberOfItems);
        sagaRegistry.put(sagaId, instance);

        log.info("[ORCHESTRATION] Starting saga sagaId={} -- customer={}, product={}, quantity={}",
                sagaId, customerId, productId, numberOfItems);

        // Send command to Neo4j service to create the PURCHASED relationship
        CreatePurchaseCommand cmd = new CreatePurchaseCommand(sagaId, customerId, productId, numberOfItems);
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.CREATE_PURCHASE_CMD_KEY,
                    cmd);
            log.info("[ORCHESTRATION] sagaId={} -- CreatePurchaseCommand sent", sagaId);
        } catch (Exception e) {
            log.error("[ORCHESTRATION] sagaId={} -- ERROR sending CreatePurchaseCommand: {}",
                    sagaId, e.getMessage(), e);
            instance.setState(SagaState.FAILED);
        }

        return sagaId;
    }

    // =========================================================================
    // Handle reply from Neo4j service (step 1)
    // =========================================================================

    /**
     * Receives the reply from the Neo4j service for CreatePurchaseCommand.
     *
     * Success: transitions to PURCHASE_CREATED and sends UpdateProductCommand to ES.
     * Failure: transitions to FAILED (Neo4j step never completed, nothing to compensate).
     *
     * @param reply PurchaseCreatedReply containing sagaId, success flag, and optional error message
     */
    @RabbitListener(queues = RabbitMQConfig.PURCHASE_CREATED_REPLY_QUEUE)
    public void handlePurchaseCreatedReply(PurchaseCreatedReply reply) {
        log.info("[ORCHESTRATION] Received PurchaseCreatedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Unknown saga sagaId={} -- ignoring reply", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setState(SagaState.PURCHASE_CREATED);
            log.info("[ORCHESTRATION] sagaId={} -> state: PURCHASE_CREATED, sending UpdateProductCommand",
                    reply.getSagaId());

            // Send command to ES service to increment purchaseCount
            UpdateProductCommand cmd = new UpdateProductCommand(reply.getSagaId(), instance.getProductId());
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                        RabbitMQConfig.UPDATE_PRODUCT_CMD_KEY,
                        cmd);
                log.info("[ORCHESTRATION] sagaId={} -- UpdateProductCommand sent", reply.getSagaId());
            } catch (Exception e) {
                log.error("[ORCHESTRATION] sagaId={} -- ERROR sending UpdateProductCommand: {}",
                        reply.getSagaId(), e.getMessage(), e);
                // Neo4j write succeeded but the ES command could not be delivered -- trigger compensation
                triggerCompensation(instance);
            }

        } else {
            log.error("[ORCHESTRATION] sagaId={} -- Neo4j step FAILED: {}",
                    reply.getSagaId(), reply.getErrorMessage());
            instance.setState(SagaState.FAILED);
        }
    }

    // =========================================================================
    // Handle reply from Elasticsearch service (step 2)
    // =========================================================================

    /**
     * Receives the reply from the ES service for UpdateProductCommand.
     *
     * Success: saga transitions to COMPLETED.
     * Failure: triggers compensation by sending DeletePurchaseCommand to Neo4j.
     *
     * @param reply ProductUpdatedReply containing sagaId, success flag, and optional error message
     */
    @RabbitListener(queues = RabbitMQConfig.PRODUCT_UPDATED_REPLY_QUEUE)
    public void handleProductUpdatedReply(ProductUpdatedReply reply) {
        log.info("[ORCHESTRATION] Received ProductUpdatedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Unknown saga sagaId={} -- ignoring reply", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setState(SagaState.PRODUCT_UPDATED);
            instance.setState(SagaState.COMPLETED);
            log.info("[ORCHESTRATION] sagaId={} -> COMPLETED -- distributed transaction finished successfully",
                    reply.getSagaId());
        } else {
            log.error("[ORCHESTRATION] sagaId={} -- ES step FAILED: {}, triggering compensation",
                    reply.getSagaId(), reply.getErrorMessage());
            triggerCompensation(instance);
        }
    }

    // =========================================================================
    // Handle compensation confirmation from Neo4j service
    // =========================================================================

    /**
     * Receives confirmation that Neo4j deleted the PURCHASED relationship (compensation complete).
     * Transitions the saga to COMPENSATED state.
     *
     * @param reply PurchaseDeletedReply containing sagaId and success flag
     */
    @RabbitListener(queues = RabbitMQConfig.PURCHASE_DELETED_REPLY_QUEUE)
    public void handlePurchaseDeletedReply(PurchaseDeletedReply reply) {
        log.info("[ORCHESTRATION] Received PurchaseDeletedReply -- sagaId={}, success={}",
                reply.getSagaId(), reply.isSuccess());

        SagaInstance instance = sagaRegistry.get(reply.getSagaId());
        if (instance == null) {
            log.error("[ORCHESTRATION] Unknown saga sagaId={} -- ignoring reply", reply.getSagaId());
            return;
        }

        if (reply.isSuccess()) {
            instance.setState(SagaState.COMPENSATED);
            log.warn("[ORCHESTRATION] sagaId={} -> COMPENSATED -- Neo4j compensation succeeded, saga cancelled",
                    reply.getSagaId());
        } else {
            instance.setState(SagaState.FAILED);
            log.error("[ORCHESTRATION] sagaId={} -> FAILED -- compensation did NOT succeed! System is in an inconsistent state!",
                    reply.getSagaId());
        }
    }

    // =========================================================================
    // Helper methods
    // =========================================================================

    /**
     * Initiates the compensation phase by sending DeletePurchaseCommand to the Neo4j service.
     */
    private void triggerCompensation(SagaInstance instance) {
        instance.setState(SagaState.COMPENSATING);
        log.warn("[ORCHESTRATION] sagaId={} -> COMPENSATING -- sending DeletePurchaseCommand",
                instance.getSagaId());

        DeletePurchaseCommand cmd = new DeletePurchaseCommand(
                instance.getSagaId(), instance.getCustomerId(), instance.getProductId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.DELETE_PURCHASE_CMD_KEY,
                    cmd);
            log.info("[ORCHESTRATION] sagaId={} -- DeletePurchaseCommand sent", instance.getSagaId());
        } catch (Exception e) {
            log.error("[ORCHESTRATION] sagaId={} -- CRITICAL: DeletePurchaseCommand could not be delivered: {}",
                    instance.getSagaId(), e.getMessage(), e);
            instance.setState(SagaState.FAILED);
        }
    }

    /**
     * Returns the current state of a saga instance for the given sagaId.
     * Useful for status polling and debugging via the REST controller.
     *
     * @param sagaId saga identifier
     * @return SagaInstance, or null if not found
     */
    public SagaInstance getSagaStatus(String sagaId) {
        return sagaRegistry.get(sagaId);
    }
}
