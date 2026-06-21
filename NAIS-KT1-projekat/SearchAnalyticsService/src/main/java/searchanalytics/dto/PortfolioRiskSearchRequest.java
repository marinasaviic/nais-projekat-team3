package searchanalytics.dto;

public class PortfolioRiskSearchRequest {
    private String text;
    private String market;
    private String dosageForm;
    private String lifecycleStatus;
    private String licenseStatus;
    private String activeIngredient;
    private Integer maxAvailableQuantity;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy = "availableQuantity";
    private String sortDirection = "asc";
    private int page = 0;
    private int size = 10;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }

    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }

    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }

    public String getActiveIngredient() { return activeIngredient; }
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient = activeIngredient; }

    public Integer getMaxAvailableQuantity() { return maxAvailableQuantity; }
    public void setMaxAvailableQuantity(Integer maxAvailableQuantity) { this.maxAvailableQuantity = maxAvailableQuantity; }

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}