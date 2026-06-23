package timeseries.dto;

public class ActivationDurationResponse {

    private String pricelistId;
    private String teamId;
    private String region;
    private long activationDurationMs;
    private double activationDurationHours;

    public ActivationDurationResponse() {
    }

    public ActivationDurationResponse(String pricelistId, String teamId, String region, long activationDurationMs) {
        this.pricelistId = pricelistId;
        this.teamId = teamId;
        this.region = region;
        this.activationDurationMs = activationDurationMs;
        this.activationDurationHours = activationDurationMs / 3_600_000.0;
    }

    public String getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(String pricelistId) {
        this.pricelistId = pricelistId;
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

    public long getActivationDurationMs() {
        return activationDurationMs;
    }

    public void setActivationDurationMs(long activationDurationMs) {
        this.activationDurationMs = activationDurationMs;
        this.activationDurationHours = activationDurationMs / 3_600_000.0;
    }

    public double getActivationDurationHours() {
        return activationDurationHours;
    }

    public void setActivationDurationHours(double activationDurationHours) {
        this.activationDurationHours = activationDurationHours;
    }
}
