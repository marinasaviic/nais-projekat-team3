package timeseries.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "price_list_lifecycle")
public class PriceListLifecycleEvent {

    @Column(name = "saga_id", tag = true)
    private String sagaId;

    @Column(name = "pricelistId", tag = true)
    private String pricelistId;

    @Column(name = "price_list_id", tag = true)
    private String priceListId;

    @Column(name = "price_list_name", tag = true)
    private String priceListName;

    @Column(name = "team_id", tag = true)
    private String teamId;

    @Column(name = "team_name", tag = true)
    private String teamName;

    @Column(name = "user_id", tag = true)
    private String userId;

    @Column(name = "user_name", tag = true)
    private String userName;

    @Column(name = "region", tag = true)
    private String region;

    @Column(name = "operationType", tag = true)
    private String operationType;

    @Column(name = "statusFrom", tag = true)
    private String statusFromTag;

    @Column(name = "statusTo", tag = true)
    private String statusToTag;

    @Column(name = "status_from")
    private String statusFrom;

    @Column(name = "status_to")
    private String statusTo;

    @Column(name = "transition_label", tag = true)
    private String transitionLabel;

    @Column(name = "duration_ms")
    private Double durationMs;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(timestamp = true)
    private Instant timestamp;

    public PriceListLifecycleEvent() {
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getPricelistId() {
        return pricelistId;
    }

    public void setPricelistId(String pricelistId) {
        this.pricelistId = pricelistId;
    }

    public String getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(String priceListId) {
        this.priceListId = priceListId;
    }

    public String getPriceListName() {
        return priceListName;
    }

    public void setPriceListName(String priceListName) {
        this.priceListName = priceListName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getStatusFromTag() {
        return statusFromTag;
    }

    public void setStatusFromTag(String statusFromTag) {
        this.statusFromTag = statusFromTag;
    }

    public String getStatusToTag() {
        return statusToTag;
    }

    public void setStatusToTag(String statusToTag) {
        this.statusToTag = statusToTag;
    }

    public String getStatusFrom() {
        return statusFrom;
    }

    public void setStatusFrom(String statusFrom) {
        this.statusFrom = statusFrom;
    }

    public String getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(String statusTo) {
        this.statusTo = statusTo;
    }

    public String getTransitionLabel() {
        return transitionLabel;
    }

    public void setTransitionLabel(String transitionLabel) {
        this.transitionLabel = transitionLabel;
    }

    public Double getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Double durationMs) {
        this.durationMs = durationMs;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
