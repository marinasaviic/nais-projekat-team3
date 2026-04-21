package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.command;

/**
 * Kompenzaciona komanda – SagaOrchestrator je šalje Neo4j servisu kada
 * ažuriranje purchaseCount u ES-u ne uspe.
 * Nalaže Neo4j servisu da obriše PURCHASED relaciju (rollback prvog koraka).
 *
 * Polja:
 *   sagaId     – identifikator sage (korelacija)
 *   customerId – ID kupca čija se relacija briše
 *   productId  – ID proizvoda
 */
public class DeletePurchaseCommand {

    private String sagaId;
    private Long customerId;
    private String productId;

    public DeletePurchaseCommand() {}

    public DeletePurchaseCommand(String sagaId, Long customerId, String productId) {
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long customerId)   { this.customerId = customerId; }

    public String getProductId()                 { return productId; }
    public void setProductId(String productId)   { this.productId = productId; }
}
