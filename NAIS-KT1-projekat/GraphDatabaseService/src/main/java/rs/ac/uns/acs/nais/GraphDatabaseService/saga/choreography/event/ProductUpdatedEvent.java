package rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.event;

/**
 * Događaj koji ElasticSearch servis objavljuje nakon uspešnog ažuriranja purchaseCount.
 * Neo4j servis može slušati ovaj događaj radi logovanja ili praćenja toka.
 *
 * Polja:
 *   sagaId    – identifikator saga instance
 *   productId – ID proizvoda čiji je purchaseCount ažuriran
 */
public class ProductUpdatedEvent {

    private String sagaId;
    private String productId;

    public ProductUpdatedEvent() {}

    public ProductUpdatedEvent(String sagaId, String productId) {
        this.sagaId = sagaId;
        this.productId = productId;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public String getProductId()                { return productId; }
    public void setProductId(String productId)  { this.productId = productId; }
}
