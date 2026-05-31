package searchanalytics.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductSearchDocument {
    private String productId;
    private String name;
    private String code;
    private String description;
    private String category;
    private String subcategory;
    private String therapeuticArea;
    private String lifecycleStatus;
    private String manufacturer;
    private List<String> tags;
    private Integer numberOfVariants;
    private Integer marketsCount;
    private BigDecimal averageVariantPrice;
    private LocalDate createdAt;

    public ProductSearchDocument() {}

    public ProductSearchDocument(String productId, String name, String code, String description, String category, String subcategory, String therapeuticArea, String lifecycleStatus, String manufacturer, List<String> tags, Integer numberOfVariants, Integer marketsCount, BigDecimal averageVariantPrice, LocalDate createdAt) {
        this.productId = productId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.therapeuticArea = therapeuticArea;
        this.lifecycleStatus = lifecycleStatus;
        this.manufacturer = manufacturer;
        this.tags = tags;
        this.numberOfVariants = numberOfVariants;
        this.marketsCount = marketsCount;
        this.averageVariantPrice = averageVariantPrice;
        this.createdAt = createdAt;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
    public String getTherapeuticArea() { return therapeuticArea; }
    public void setTherapeuticArea(String therapeuticArea) { this.therapeuticArea = therapeuticArea; }
    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Integer getNumberOfVariants() { return numberOfVariants; }
    public void setNumberOfVariants(Integer numberOfVariants) { this.numberOfVariants = numberOfVariants; }
    public Integer getMarketsCount() { return marketsCount; }
    public void setMarketsCount(Integer marketsCount) { this.marketsCount = marketsCount; }
    public BigDecimal getAverageVariantPrice() { return averageVariantPrice; }
    public void setAverageVariantPrice(BigDecimal averageVariantPrice) { this.averageVariantPrice = averageVariantPrice; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
