package salesanalytics.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "sales_process_event")
public class SalesProcessEvent {

    @Column(name = "opportunity_id", tag = true)
    private String opportunityId;

    @Column(name = "customer_id", tag = true)
    private String customerId;

    @Column(name = "customer_segment", tag = true)
    private String customerSegment;

    @Column(name = "sales_rep_id", tag = true)
    private String salesRepId;

    @Column(name = "sales_rep_name", tag = true)
    private String salesRepName;

    @Column(name = "region", tag = true)
    private String region;

    @Column(name = "product_category", tag = true)
    private String productCategory;

    @Column(name = "stage_from", tag = true)
    private String stageFrom;

    @Column(name = "stage_to", tag = true)
    private String stageTo;

    @Column(name = "activity_type", tag = true)
    private String activityType;

    @Column(name = "outcome", tag = true)
    private String outcome;

    @Column(name = "deal_value")
    private Double dealValue;

    @Column(name = "probability")
    private Double probability;

    @Column(name = "stage_duration_hours")
    private Double stageDurationHours;

    @Column(name = "activity_duration_minutes")
    private Double activityDurationMinutes;

    @Column(timestamp = true)
    private Instant timestamp;

    public SalesProcessEvent() {}

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerSegment() { return customerSegment; }
    public void setCustomerSegment(String customerSegment) { this.customerSegment = customerSegment; }

    public String getSalesRepId() { return salesRepId; }
    public void setSalesRepId(String salesRepId) { this.salesRepId = salesRepId; }

    public String getSalesRepName() { return salesRepName; }
    public void setSalesRepName(String salesRepName) { this.salesRepName = salesRepName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public String getStageFrom() { return stageFrom; }
    public void setStageFrom(String stageFrom) { this.stageFrom = stageFrom; }

    public String getStageTo() { return stageTo; }
    public void setStageTo(String stageTo) { this.stageTo = stageTo; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public Double getDealValue() { return dealValue; }
    public void setDealValue(Double dealValue) { this.dealValue = dealValue; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public Double getStageDurationHours() { return stageDurationHours; }
    public void setStageDurationHours(Double stageDurationHours) { this.stageDurationHours = stageDurationHours; }

    public Double getActivityDurationMinutes() { return activityDurationMinutes; }
    public void setActivityDurationMinutes(Double activityDurationMinutes) { this.activityDurationMinutes = activityDurationMinutes; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}