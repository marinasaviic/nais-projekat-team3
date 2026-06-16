package searchanalytics.dto;

public class MarketAvailabilityReportDto {
    private String market;
    private Double avgPrice;
    private Double totalAvailableQuantity;
    private Double minAvailableQuantity;

    public MarketAvailabilityReportDto() {
    }

    public MarketAvailabilityReportDto(String market, Double avgPrice,
                                       Double totalAvailableQuantity,
                                       Double minAvailableQuantity) {
        this.market = market;
        this.avgPrice = avgPrice;
        this.totalAvailableQuantity = totalAvailableQuantity;
        this.minAvailableQuantity = minAvailableQuantity;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getTotalAvailableQuantity() {
        return totalAvailableQuantity;
    }

    public void setTotalAvailableQuantity(Double totalAvailableQuantity) {
        this.totalAvailableQuantity = totalAvailableQuantity;
    }

    public Double getMinAvailableQuantity() {
        return minAvailableQuantity;
    }

    public void setMinAvailableQuantity(Double minAvailableQuantity) {
        this.minAvailableQuantity = minAvailableQuantity;
    }
}