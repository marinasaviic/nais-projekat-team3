package collab.saga.dto;

public class DeletePricelistCommand {

    private String userId;
    private String teamId;
    private String region;

    public DeletePricelistCommand() {
    }

    public DeletePricelistCommand(String userId, String teamId, String region) {
        this.userId = userId;
        this.teamId = teamId;
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
