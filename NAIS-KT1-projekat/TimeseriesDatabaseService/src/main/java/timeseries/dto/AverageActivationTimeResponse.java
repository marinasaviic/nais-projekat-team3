package timeseries.dto;

public class AverageActivationTimeResponse {

    private String teamId;
    private String region;
    private long activatedPricelistCount;
    private double averageActivationTimeMs;
    private double averageActivationTimeHours;

    public AverageActivationTimeResponse() {
    }

    public AverageActivationTimeResponse(String teamId, String region, long activatedPricelistCount, double averageActivationTimeMs) {
        this.teamId = teamId;
        this.region = region;
        this.activatedPricelistCount = activatedPricelistCount;
        this.averageActivationTimeMs = averageActivationTimeMs;
        this.averageActivationTimeHours = averageActivationTimeMs / 3_600_000.0;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
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
}
