package searchanalytics.dto;

import java.util.List;
import java.util.Map;

public class SearchResponse {
    private long total;
    private List<Map<String, Object>> hits;
    private Map<String, Object> aggregations;

    public SearchResponse() {}

    public SearchResponse(long total, List<Map<String, Object>> hits, Map<String, Object> aggregations) {
        this.total = total;
        this.hits = hits;
        this.aggregations = aggregations;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<Map<String, Object>> getHits() { return hits; }
    public void setHits(List<Map<String, Object>> hits) { this.hits = hits; }
    public Map<String, Object> getAggregations() { return aggregations; }
    public void setAggregations(Map<String, Object> aggregations) { this.aggregations = aggregations; }
}
