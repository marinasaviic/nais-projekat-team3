package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

/**
 * Komanda koju SagaOrchestrator šalje Neo4j servisu.
 * Nalaže Neo4j servisu da kreira PURCHASED relaciju između kupca i proizvoda.
 *
 * Polja:
 *   sagaId       – identifikator saga instance (koristi se za korelaciju odgovora)
 *   customerId   – ID kupca
 *   productId    – ID proizvoda
 *   numberOfItems – broj stavki kupovine
 */
public class CreatePurchaseCommand {

    private String sagaId;
    private Long customerId;
    private String productId;
    private Integer numberOfItems;

    public CreatePurchaseCommand() {}

    public CreatePurchaseCommand(String sagaId, Long customerId, String productId, Integer numberOfItems) {
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
        this.numberOfItems = numberOfItems;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long customerId)   { this.customerId = customerId; }

    public String getProductId()                 { return productId; }
    public void setProductId(String productId)   { this.productId = productId; }

    public Integer getNumberOfItems()                   { return numberOfItems; }
    public void setNumberOfItems(Integer numberOfItems) { this.numberOfItems = numberOfItems; }
}
