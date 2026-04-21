package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

/**
 * Komanda koju SagaOrchestrator šalje ElasticSearch servisu.
 * Nalaže ES servisu da inkrementuje purchaseCount za dati proizvod.
 *
 * Polja:
 *   sagaId    – identifikator sage (korelacija)
 *   productId – ID proizvoda čiji se purchaseCount uvećava
 */
public class UpdateProductCommand {

    private String sagaId;
    private String productId;

    public UpdateProductCommand() {}

    public UpdateProductCommand(String sagaId, String productId) {
        this.sagaId = sagaId;
        this.productId = productId;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public String getProductId()                { return productId; }
    public void setProductId(String productId)  { this.productId = productId; }
}
