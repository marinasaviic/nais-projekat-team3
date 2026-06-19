package collab.report.dto;

public class CollaborativePricelistSummaryRow {

    private String pricelistId;
    private String name;
    private String region;
    private String teamId;
    private String teamName;
    private String currentStatus;
    private String creatorUserId;
    private Integer numberOfCollaborators;

    public String getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(String pricelistId) {
        this.pricelistId = pricelistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public Integer getNumberOfCollaborators() {
        return numberOfCollaborators;
    }

    public void setNumberOfCollaborators(Integer numberOfCollaborators) {
        this.numberOfCollaborators = numberOfCollaborators;
    }
}
