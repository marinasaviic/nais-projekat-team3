package timeseries.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "price_list_lifecycle")
public class PriceListLifecycleEvent {

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

    // These must be fields (not tags) per spec
    @Column(name = "status_from")
    private String statusFrom;

    @Column(name = "status_to")
    private String statusTo;

    @Column(name = "transition_label", tag = true)
    private String transitionLabel;

    @Column(name = "duration_ms")
    private Double durationMs;

    @Column(timestamp = true)
    private Instant timestamp;

    public PriceListLifecycleEvent() {
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}