package collab.report.dto;

public class ActivationPerformanceRow {

    private String teamId;
    private String teamName;
    private String region;
    private long activatedPricelistCount;
    private double averageActivationTimeMs;
    private double averageActivationTimeHours;
    private double fastestActivationHours;
    private double slowestActivationHours;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getActivatedPricelistCount() {
        return activatedPricelistCount;
    }

    public void setActivatedPricelistCount(long activatedPricelistCount) {
        this.activatedPricelistCount = activatedPricelistCount;
    }

    public double getAverageActivationTimeMs() {
        return averageActivationTimeMs;
    }

    public void setAverageActivationTimeMs(double averageActivationTimeMs) {
        this.averageActivationTimeMs = averageActivationTimeMs;
        this.averageActivationTimeHours = averageActivationTimeMs / 3_600_000.0;
    }

    public double getAverageActivationTimeHours() {
        return averageActivationTimeHours;
    }

    public void setAverageActivationTimeHours(double averageActivationTimeHours) {
        this.averageActivationTimeHours = averageActivationTimeHours;
    }

    public double getFastestActivationHours() {
        return fastestActivationHours;
    }

    public void setFastestActivationHours(double fastestActivationHours) {
        this.fastestActivationHours = fastestActivationHours;
    }

    public double getSlowestActivationHours() {
        return slowestActivationHours;
    }

    public void setSlowestActivationHours(double slowestActivationHours) {
        this.slowestActivationHours = slowestActivationHours;
    }
}
