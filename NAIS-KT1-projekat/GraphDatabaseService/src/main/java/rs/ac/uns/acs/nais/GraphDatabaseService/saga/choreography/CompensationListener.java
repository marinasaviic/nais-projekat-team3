package rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.event.ProductUpdateFailedEvent;

/**
 * Compensation listener for the choreography-based Saga.
 *
 * Listens on "product.update.failed.queue". When the Elasticsearch service fails to
 * increment purchaseCount, it publishes a ProductUpdateFailedEvent. This listener
 * responds by deleting the PURCHASED relationship from Neo4j -- the compensating
 * transaction that undoes step 1 of the saga.
 *
 * The delete is idempotent: the handler checks whether the relationship exists before
 * attempting to remove it, so duplicate events are handled safely.
 */
@Slf4j
@Component
public class CompensationListener {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    public CompensationListener(CustomerRepository customerRepository, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Receives ProductUpdateFailedEvent and deletes the PURCHASED relationship from Neo4j.
     * After a successful delete, publishes a confirmation event to "purchase.compensated.queue".
     *
     * @param event contains sagaId, customerId, productId, and the reason for failure
     */
    @RabbitListener(queues = RabbitMQConfig.PRODUCT_UPDATE_FAILED_QUEUE)
    public void handleProductUpdateFailed(ProductUpdateFailedEvent event) {
        log.warn("[CHOREOGRAPHY][COMPENSATION] Received ProductUpdateFailedEvent -- sagaId={}, customer={}, product={}, reason={}",
                event.getSagaId(), event.getCustomerId(), event.getProductId(), event.getReason());

        try {
            // Idempotent delete -- only remove if the relationship actually exists
            boolean exists = customerRepository.hasPurchasedProduct(
                    event.getCustomerId(), event.getProductId());

            if (exists) {
                customerRepository.deletePurchase(event.getCustomerId(), event.getProductId());
                log.info("[CHOREOGRAPHY][COMPENSATION] sagaId={} -- PURCHASED relationship deleted from Neo4j",
                        event.getSagaId());
            } else {
                log.warn("[CHOREOGRAPHY][COMPENSATION] sagaId={} -- relationship not found, no action needed",
                        event.getSagaId());
            }

            // Publish compensation confirmation (optional, useful for audit/tracing)
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CHOREOGRAPHY_EXCHANGE,
                    RabbitMQConfig.PURCHASE_COMPENSATED_KEY,
                    event.getSagaId());
            log.info("[CHOREOGRAPHY][COMPENSATION] sagaId={} -- PurchaseCompensated event published",
                    event.getSagaId());

        } catch (Exception e) {
            log.error("[CHOREOGRAPHY][COMPENSATION] sagaId={} -- ERROR during compensation: {}",
                    event.getSagaId(), e.getMessage(), e);
        }
    }
}
