package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.reply;

/**
 * Odgovor koji Neo4j servis šalje SagaOrchestratoru nakon obrade DeletePurchaseCommand.
 * Potvrđuje da je kompenzaciona transakcija (brisanje PURCHASED relacije) završena.
 *
 * Polja:
 *   sagaId  – identifikator sage (korelacija)
 *   success – true ako je PURCHASED relacija uspešno obrisana
 */
public class PurchaseDeletedReply {

    private String sagaId;
    private boolean success;

    public PurchaseDeletedReply() {}

    public PurchaseDeletedReply(String sagaId, boolean success) {
        this.sagaId = sagaId;
        this.success = success;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public boolean isSuccess()               { return success; }
    public void setSuccess(boolean success)  { this.success = success; }
}
