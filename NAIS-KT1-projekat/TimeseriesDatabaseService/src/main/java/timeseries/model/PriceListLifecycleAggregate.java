package timeseries.model;

import java.time.Instant;

public class PriceListLifecycleAggregate {

    private String groupKey;
    private String metric;
    private Double value;
    private Instant timestamp;

    public PriceListLifecycleAggregate() {
    }

    public PriceListLifecycleAggregate(String groupKey, String metric, Double value) {
        this.groupKey = groupKey;
        this.metric = metric;
        this.value = value;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}