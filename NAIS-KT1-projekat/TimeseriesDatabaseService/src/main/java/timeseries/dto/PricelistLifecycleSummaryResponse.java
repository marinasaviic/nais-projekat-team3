package timeseries.dto;

public class PricelistLifecycleSummaryResponse {

    private long totalEvents;
    private long createdCount;
    private long statusChangedCount;
    private long completedCount;
    private long failedCount;
    private double averageDurationMs;

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(long createdCount) {
        this.createdCount = createdCount;
    }

    public long getStatusChangedCount() {
        return statusChangedCount;
    }

    public void setStatusChangedCount(long statusChangedCount) {
        this.statusChangedCount = statusChangedCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public double getAverageDurationMs() {
        return averageDurationMs;
    }

    public void setAverageDurationMs(double averageDurationMs) {
        this.averageDurationMs = averageDurationMs;
    }
}
