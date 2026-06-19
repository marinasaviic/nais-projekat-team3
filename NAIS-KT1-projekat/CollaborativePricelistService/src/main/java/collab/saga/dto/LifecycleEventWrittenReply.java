package collab.saga.dto;

public class LifecycleEventWrittenReply {

    private String sagaId;
    private boolean success;
    private String errorMessage;

    public LifecycleEventWrittenReply() {
    }

    public LifecycleEventWrittenReply(String sagaId, boolean success, String errorMessage) {
        this.sagaId = sagaId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
