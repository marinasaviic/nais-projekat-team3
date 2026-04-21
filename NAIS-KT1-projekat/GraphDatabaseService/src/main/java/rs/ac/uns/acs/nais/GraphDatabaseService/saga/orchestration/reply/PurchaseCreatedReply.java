package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

/**
 * Odgovor koji Neo4j servis šalje SagaOrchestratoru nakon obrade CreatePurchaseCommand.
 *
 * Polja:
 *   sagaId       – identifikator sage (korelacija)
 *   success      – true ako je PURCHASED relacija uspešno kreirana
 *   errorMessage – opis greške (null ako je success=true)
 */
public class PurchaseCreatedReply {

    private String sagaId;
    private boolean success;
    private String errorMessage;

    public PurchaseCreatedReply() {}

    public PurchaseCreatedReply(String sagaId, boolean success, String errorMessage) {
        this.sagaId = sagaId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public boolean isSuccess()               { return success; }
    public void setSuccess(boolean success)  { this.success = success; }

    public String getErrorMessage()                    { return errorMessage; }
    public void setErrorMessage(String errorMessage)   { this.errorMessage = errorMessage; }
}
