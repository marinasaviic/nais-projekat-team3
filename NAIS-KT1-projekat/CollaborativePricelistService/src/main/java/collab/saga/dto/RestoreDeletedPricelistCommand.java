package collab.saga.dto;

import collab.model.Pricelist;

public class RestoreDeletedPricelistCommand {

    private String sagaId;
    private Pricelist pricelist;

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public Pricelist getPricelist() {
        return pricelist;
    }

    public void setPricelist(Pricelist pricelist) {
        this.pricelist = pricelist;
    }
}
