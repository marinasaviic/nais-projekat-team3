package rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.event;

/**
 * Događaj koji ElasticSearch servis objavljuje kada ažuriranje purchaseCount nije uspelo.
 * Neo4j servis sluša ovaj događaj i pokreće kompenzaciju – briše PURCHASED relaciju.
 *
 * Polja:
 *   sagaId     – identifikator saga instance
 *   customerId – ID kupca (potreban za pronalaženje relacije koja se briše)
 *   productId  – ID proizvoda
 *   reason     – opis razloga greške
 */
public class ProductUpdateFailedEvent {

    private String sagaId;
    private Long customerId;
    private String productId;
    private String reason;

    public ProductUpdateFailedEvent() {}

    public ProductUpdateFailedEvent(String sagaId, Long customerId, String productId, String reason) {
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
        this.reason = reason;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long customerId)   { this.customerId = customerId; }

    public String getProductId()                 { return productId; }
    public void setProductId(String productId)   { this.productId = productId; }

    public String getReason()              { return reason; }
    public void setReason(String reason)   { this.reason = reason; }
}
