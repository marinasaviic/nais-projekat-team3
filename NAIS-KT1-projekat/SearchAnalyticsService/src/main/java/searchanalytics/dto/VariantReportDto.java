package searchanalytics.dto;

import java.math.BigDecimal;

public class VariantReportDto {
    private String variantId;
    private String productId;
    private String productName;
    private String name;
    private String market;
    private BigDecimal price;
    private Integer availableQuantity;

    public VariantReportDto() {
    }

    public VariantReportDto(String variantId, String productId, String productName, String name,
                            String market, BigDecimal price, Integer availableQuantity) {
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.name = name;
        this.market = market;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}