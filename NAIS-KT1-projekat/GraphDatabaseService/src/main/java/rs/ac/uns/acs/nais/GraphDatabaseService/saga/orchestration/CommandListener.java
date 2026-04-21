package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.CreatePurchaseCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command.DeletePurchaseCommand;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.PurchaseCreatedReply;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply.PurchaseDeletedReply;

/**
 * Command listener for the orchestrated Saga on the Neo4j side (GraphDatabaseService).
 *
 * Listens on two command queues:
 *   1. "create.purchase.command.queue" -- creates the PURCHASED relationship,
 *      then sends PurchaseCreatedReply back to the orchestrator.
 *   2. "delete.purchase.command.queue" -- deletes the PURCHASED relationship as a
 *      compensating transaction, then sends PurchaseDeletedReply back.
 *
 * Both operations are idempotent: the handler checks for an existing relationship
 * before writing or deleting to avoid duplicate side-effects on retry.
 */
@Slf4j
@Component
public class CommandListener {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    public CommandListener(CustomerRepository customerRepository, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // =========================================================================
    // Create PURCHASED relationship
    // =========================================================================

    /**
     * Handles CreatePurchaseCommand from the SagaOrchestrator.
     * Creates or updates the PURCHASED relationship in Neo4j (idempotent via Cypher MERGE).
     * Sends PurchaseCreatedReply with success=true on success, success=false on error.
     *
     * @param cmd command carrying sagaId, customerId, productId, and numberOfItems
     */
    @RabbitListener(queues = RabbitMQConfig.CREATE_PURCHASE_CMD_QUEUE)
    public void handleCreatePurchaseCommand(CreatePurchaseCommand cmd) {
        log.info("[ORCHESTRATION][NEO4J] Received CreatePurchaseCommand -- sagaId={}, customer={}, product={}",
                cmd.getSagaId(), cmd.getCustomerId(), cmd.getProductId());

        PurchaseCreatedReply reply;
        try {
            // Idempotent write: increment count if relationship exists, otherwise create it
            if (customerRepository.hasPurchasedProduct(cmd.getCustomerId(), cmd.getProductId())) {
                log.info("[ORCHESTRATION][NEO4J] sagaId={} -- relationship exists, incrementing counter", cmd.getSagaId());
                customerRepository.purchaseProduct(cmd.getCustomerId(), cmd.getProductId());
            } else {
                log.info("[ORCHESTRATION][NEO4J] sagaId={} -- creating new PURCHASED relationship", cmd.getSagaId());
                customerRepository.createPurchase(cmd.getCustomerId(), cmd.getProductId());
            }
            log.info("[ORCHESTRATION][NEO4J] sagaId={} -- PURCHASED relationship saved successfully", cmd.getSagaId());
            reply = new PurchaseCreatedReply(cmd.getSagaId(), true, null);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][NEO4J] sagaId={} -- ERROR creating relationship: {}",
                    cmd.getSagaId(), e.getMessage(), e);
            reply = new PurchaseCreatedReply(cmd.getSagaId(), false, e.getMessage());
        }

        // Send reply back to the orchestrator
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.PURCHASE_CREATED_REPLY_KEY,
                    reply);
            log.info("[ORCHESTRATION][NEO4J] sagaId={} -- PurchaseCreatedReply sent (success={})",
                    cmd.getSagaId(), reply.isSuccess());
        } catch (Exception e) {
            log.error("[ORCHESTRATION][NEO4J] sagaId={} -- ERROR sending PurchaseCreatedReply: {}",
                    cmd.getSagaId(), e.getMessage(), e);
        }
    }

    // =========================================================================
    // Delete PURCHASED relationship (compensation)
    // =========================================================================

    /**
     * Handles DeletePurchaseCommand from the SagaOrchestrator.
     * Deletes the PURCHASED relationship from Neo4j as a compensating transaction.
     * Sends PurchaseDeletedReply with success=true on success, success=false on error.
     *
     * @param cmd command carrying sagaId, customerId, and productId
     */
    @RabbitListener(queues = RabbitMQConfig.DELETE_PURCHASE_CMD_QUEUE)
    public void handleDeletePurchaseCommand(DeletePurchaseCommand cmd) {
        log.warn("[ORCHESTRATION][NEO4J][COMPENSATION] Received DeletePurchaseCommand -- sagaId={}, customer={}, product={}",
                cmd.getSagaId(), cmd.getCustomerId(), cmd.getProductId());

        PurchaseDeletedReply reply;
        try {
            // Idempotent delete -- only remove if the relationship exists
            boolean exists = customerRepository.hasPurchasedProduct(cmd.getCustomerId(), cmd.getProductId());
            if (exists) {
                customerRepository.deletePurchase(cmd.getCustomerId(), cmd.getProductId());
                log.info("[ORCHESTRATION][NEO4J][COMPENSATION] sagaId={} -- PURCHASED relationship deleted",
                        cmd.getSagaId());
            } else {
                log.warn("[ORCHESTRATION][NEO4J][COMPENSATION] sagaId={} -- relationship not found, nothing to delete",
                        cmd.getSagaId());
            }
            reply = new PurchaseDeletedReply(cmd.getSagaId(), true);

        } catch (Exception e) {
            log.error("[ORCHESTRATION][NEO4J][COMPENSATION] sagaId={} -- ERROR deleting relationship: {}",
                    cmd.getSagaId(), e.getMessage(), e);
            reply = new PurchaseDeletedReply(cmd.getSagaId(), false);
        }

        // Send compensation confirmation back to the orchestrator
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORCHESTRATION_EXCHANGE,
                    RabbitMQConfig.PURCHASE_DELETED_REPLY_KEY,
                    reply);
            log.info("[ORCHESTRATION][NEO4J][COMPENSATION] sagaId={} -- PurchaseDeletedReply sent (success={})",
                    cmd.getSagaId(), reply.isSuccess());
        } catch (Exception e) {
            log.error("[ORCHESTRATION][NEO4J][COMPENSATION] sagaId={} -- ERROR sending PurchaseDeletedReply: {}",
                    cmd.getSagaId(), e.getMessage(), e);
        }
    }
}
