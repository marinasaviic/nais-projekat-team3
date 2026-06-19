package collab.report.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CollaborativePricelistLifecycleReportDto {

    private Instant generatedAt;
    private CollaborativePricelistReportFilters filters;
    private List<CollaborativePricelistSummaryRow> pricelists = new ArrayList<>();
    private List<LifecycleEventRow> lifecycleEvents = new ArrayList<>();
    private List<ActivationPerformanceRow> activationPerformance = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public CollaborativePricelistReportFilters getFilters() {
        return filters;
    }

    public void setFilters(CollaborativePricelistReportFilters filters) {
        this.filters = filters;
    }

    public List<CollaborativePricelistSummaryRow> getPricelists() {
        return pricelists;
    }

    public void setPricelists(List<CollaborativePricelistSummaryRow> pricelists) {
        this.pricelists = pricelists;
    }

    public List<LifecycleEventRow> getLifecycleEvents() {
        return lifecycleEvents;
    }

    public void setLifecycleEvents(List<LifecycleEventRow> lifecycleEvents) {
        this.lifecycleEvents = lifecycleEvents;
    }

    public List<ActivationPerformanceRow> getActivationPerformance() {
        return activationPerformance;
    }

    public void setActivationPerformance(List<ActivationPerformanceRow> activationPerformance) {
        this.activationPerformance = activationPerformance;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
