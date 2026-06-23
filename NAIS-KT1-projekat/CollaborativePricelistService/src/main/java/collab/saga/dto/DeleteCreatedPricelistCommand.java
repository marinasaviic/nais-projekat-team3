package collab.saga.dto;

public class DeleteCreatedPricelistCommand {

    private String sagaId;
    private String pricelistId;

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(String pricelistId) {
        this.pricelistId = pricelistId;
    }
}
