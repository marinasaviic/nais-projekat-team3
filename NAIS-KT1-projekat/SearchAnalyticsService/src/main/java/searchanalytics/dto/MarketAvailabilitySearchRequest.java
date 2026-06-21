package searchanalytics.dto;

import java.math.BigDecimal;

public class MarketAvailabilitySearchRequest {
    private String market;
    private String dosageForm;
    private String lifecycleStatus;
    private String licenseStatus;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minAvailableQuantity;
    private String sortBy = "price";
    private String sortDirection = "asc";
    private int page = 0;
    private int size = 10;

    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }
    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }
    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }
    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public Integer getMinAvailableQuantity() { return minAvailableQuantity; }
    public void setMinAvailableQuantity(Integer minAvailableQuantity) { this.minAvailableQuantity = minAvailableQuantity; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
