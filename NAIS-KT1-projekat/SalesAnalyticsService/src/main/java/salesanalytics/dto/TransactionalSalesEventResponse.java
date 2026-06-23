package salesanalytics.dto;

import salesanalytics.model.SalesProcessEvent;

import java.time.Instant;
import java.util.List;

public class TransactionalSalesEventResponse {

    private String sagaId;
    private String status;
    private String message;
    private SalesProcessEvent event;
    private List<String> completedSteps;
    private Instant completedAt;

    public TransactionalSalesEventResponse() {
    }

    public TransactionalSalesEventResponse(String sagaId, String status, String message,
                                           SalesProcessEvent event, List<String> completedSteps,
                                           Instant completedAt) {
        this.sagaId = sagaId;
        this.status = status;
        this.message = message;
        this.event = event;
        this.completedSteps = completedSteps;
        this.completedAt = completedAt;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SalesProcessEvent getEvent() {
        return event;
    }

    public void setEvent(SalesProcessEvent event) {
        this.event = event;
    }

    public List<String> getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(List<String> completedSteps) {
        this.completedSteps = completedSteps;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
