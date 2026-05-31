package searchanalytics.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VariantSearchDocument {
    private String variantId;
    private String productId;
    private String productName;
    private String name;
    private String dosageForm;
    private String strength;
    private String packageSize;
    private String market;
    private String licenseStatus;
    private String lifecycleStatus;
    private BigDecimal price;
    private Integer availableQuantity;
    private String activeIngredient;
    private List<String> excipients;
    private LocalDate createdAt;

    public VariantSearchDocument() {}

    public VariantSearchDocument(String variantId, String productId, String productName, String name, String dosageForm, String strength, String packageSize, String market, String licenseStatus, String lifecycleStatus, BigDecimal price, Integer availableQuantity, String activeIngredient, List<String> excipients, LocalDate createdAt) {
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.name = name;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.packageSize = packageSize;
        this.market = market;
        this.licenseStatus = licenseStatus;
        this.lifecycleStatus = lifecycleStatus;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.activeIngredient = activeIngredient;
        this.excipients = excipients;
        this.createdAt = createdAt;
    }

    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }
    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    public String getPackageSize() { return packageSize; }
    public void setPackageSize(String packageSize) { this.packageSize = packageSize; }
    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }
    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }
    public String getLifecycleStatus() { return lifecycleStatus; }
    public void setLifecycleStatus(String lifecycleStatus) { this.lifecycleStatus = lifecycleStatus; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    public String getActiveIngredient() { return activeIngredient; }
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient = activeIngredient; }
    public List<String> getExcipients() { return excipients; }
    public void setExcipients(List<String> excipients) { this.excipients = excipients; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
