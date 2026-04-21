package rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.config.RabbitMQConfig;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.event.PurchaseCreatedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entry point for the choreography-based Saga that records a product purchase.
 *
 * Choreography flow (step 1 of 2):
 *   1. Creates the PURCHASED relationship in Neo4j (idempotent via Cypher MERGE).
 *   2. Publishes a PurchaseCreatedEvent to RabbitMQ.
 *   3. The ElasticSearch service picks up the event and increments purchaseCount (step 2).
 *   4. If the ES step fails, it publishes ProductUpdateFailedEvent.
 *   5. CompensationListener in this service receives the failure and deletes the relationship.
 */
@Slf4j
@Service
public class PurchaseService {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;

    public PurchaseService(CustomerRepository customerRepository, RabbitTemplate rabbitTemplate) {
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Creates the PURCHASED relationship between a customer and a product in Neo4j,
     * then publishes a PurchaseCreatedEvent so the ES service can update purchaseCount.
     *
     * The write is idempotent: Cypher MERGE either creates the relationship or increments
     * its counter, preventing duplicate edges for the same customer/product pair.
     *
     * @param customerId    Neo4j idOriginal of the customer
     * @param productId     Neo4j idOriginal of the product
     * @param numberOfItems number of items ordered
     * @return sagaId identifying this saga instance
     */
    public String createPurchase(Long customerId, String productId, Integer numberOfItems) {
        String sagaId = UUID.randomUUID().toString();
        log.info("[CHOREOGRAPHY] Starting saga sagaId={} -- customer={}, product={}, quantity={}",
                sagaId, customerId, productId, numberOfItems);

        try {
            // Step 1: write to Neo4j -- MERGE is idempotent (create or increment)
            if (customerRepository.hasPurchasedProduct(customerId, productId)) {
                log.info("[CHOREOGRAPHY] sagaId={} -- relationship exists, incrementing counter", sagaId);
                customerRepository.purchaseProduct(customerId, productId);
            } else {
                log.info("[CHOREOGRAPHY] sagaId={} -- creating new PURCHASED relationship", sagaId);
                customerRepository.createPurchase(customerId, productId);
            }
            log.info("[CHOREOGRAPHY] sagaId={} -- PURCHASED relationship saved in Neo4j", sagaId);

        } catch (Exception e) {
            log.error("[CHOREOGRAPHY] sagaId={} -- ERROR writing to Neo4j: {}", sagaId, e.getMessage(), e);
            throw new RuntimeException("Neo4j write failed for sagaId=" + sagaId, e);
        }

        // Step 2: publish event so the ES service updates purchaseCount
        PurchaseCreatedEvent event = new PurchaseCreatedEvent(
                sagaId, customerId, productId, numberOfItems, LocalDateTime.now());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CHOREOGRAPHY_EXCHANGE,
                    RabbitMQConfig.PURCHASE_CREATED_KEY,
                    event);
            log.info("[CHOREOGRAPHY] sagaId={} -- PurchaseCreatedEvent published on exchange={}, key={}",
                    sagaId, RabbitMQConfig.CHOREOGRAPHY_EXCHANGE, RabbitMQConfig.PURCHASE_CREATED_KEY);
        } catch (Exception e) {
            log.error("[CHOREOGRAPHY] sagaId={} -- ERROR publishing PurchaseCreatedEvent: {}",
                    sagaId, e.getMessage(), e);
            // RabbitMQ delivery failed -- compensate the Neo4j write immediately
            log.warn("[CHOREOGRAPHY] sagaId={} -- triggering immediate compensation due to messaging failure", sagaId);
            customerRepository.deletePurchase(customerId, productId);
            throw new RuntimeException("RabbitMQ delivery failed for sagaId=" + sagaId, e);
        }

        return sagaId;
    }
}
