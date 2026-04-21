package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

import java.time.LocalDateTime;

/**
 * Represents a single running instance of the orchestrated Saga.
 * Holds all context needed to track and drive one distributed purchase transaction.
 *
 * Each HTTP request to POST /api/purchase/orchestrated produces one SagaInstance.
 * Instances live in the in-memory ConcurrentHashMap inside SagaOrchestrator.
 *
 * Fields:
 *   sagaId        -- UUID string, used as the correlation key for all RabbitMQ messages
 *   state         -- current lifecycle state (see SagaState enum)
 *   customerId    -- ID of the customer initiating the purchase
 *   productId     -- ID of the product being purchased
 *   numberOfItems -- quantity ordered
 *   createdAt     -- timestamp of saga creation
 */
public class SagaInstance {

    private String sagaId;
    private SagaState state;
    private Long customerId;
    private String productId;
    private Integer numberOfItems;
    private LocalDateTime createdAt;

    public SagaInstance() {}

    public SagaInstance(String sagaId, Long customerId, String productId, Integer numberOfItems) {
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
        this.numberOfItems = numberOfItems;
        this.state = SagaState.STARTED;
        this.createdAt = LocalDateTime.now();
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public SagaState getState()              { return state; }
    public void setState(SagaState state)    { this.state = state; }

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long customerId)   { this.customerId = customerId; }

    public String getProductId()                 { return productId; }
    public void setProductId(String productId)   { this.productId = productId; }

    public Integer getNumberOfItems()                   { return numberOfItems; }
    public void setNumberOfItems(Integer numberOfItems) { this.numberOfItems = numberOfItems; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "SagaInstance{sagaId='" + sagaId + "', state=" + state +
               ", customerId=" + customerId + ", productId='" + productId + "'}";
    }
}
