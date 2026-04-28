package rs.ac.uns.acs.nais.CollaborativePricelistService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("ActivityLog")
public class ActivityLog {

    @Id
    private String id;
    private String actionType;
    private LocalDateTime timestamp;
    private Integer durationMinutes;
    private String details;
    private String userId;
    private String teamId;
    private String pricelistId;
    private String regionId;

    public ActivityLog() {
    }

    public ActivityLog(String id, String actionType, LocalDateTime timestamp, Integer durationMinutes, String details,
                       String userId, String teamId, String pricelistId, String regionId) {
        this.id = id;
        this.actionType = actionType;
        this.timestamp = timestamp;
        this.durationMinutes = durationMinutes;
        this.details = details;
        this.userId = userId;
        this.teamId = teamId;
        this.pricelistId = pricelistId;
        this.regionId = regionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public String getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(String pricelistId) {
        this.pricelistId = pricelistId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
