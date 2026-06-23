package rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto;

import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;

import java.time.Instant;
import java.util.List;

public class AdverseReportSagaResponse {
    private String sagaId;
    private String status;
    private String message;
    private AdverseEventReportDocument report;
    private List<String> completedSteps;
    private Instant completedAt;

    public AdverseReportSagaResponse() {
    }

    public AdverseReportSagaResponse(String sagaId, String status, String message,
                                     AdverseEventReportDocument report, List<String> completedSteps,
                                     Instant completedAt) {
        this.sagaId = sagaId;
        this.status = status;
        this.message = message;
        this.report = report;
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

    public AdverseEventReportDocument getReport() {
        return report;
    }

    public void setReport(AdverseEventReportDocument report) {
        this.report = report;
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
