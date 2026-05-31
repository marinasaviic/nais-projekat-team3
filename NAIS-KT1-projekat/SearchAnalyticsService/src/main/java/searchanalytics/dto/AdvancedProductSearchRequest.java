package searchanalytics.dto;

public class AdvancedProductSearchRequest {
    private String text;
    private String lifecycleStatus;
    private String therapeuticArea;
    private String category;
    private String manufacturer;
    private Integer minVariants;
    private Integer minMarkets;
    private String sortBy = "numberOfVariants";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 10;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }
    public String getTherapeuticArea() { return therapeuticArea; }
    public void setTherapeuticArea(String therapeuticArea) { this.therapeuticArea = therapeuticArea; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Integer getMinVariants() { return minVariants; }
    public void setMinVariants(Integer minVariants) { this.minVariants = minVariants; }
    public Integer getMinMarkets() { return minMarkets; }
    public void setMinMarkets(Integer minMarkets) { this.minMarkets = minMarkets; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
