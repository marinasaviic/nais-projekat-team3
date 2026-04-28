package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.ProductVariant;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/variants")
public class ProductVariantController {

    private final ProductPortfolioService productPortfolioService;

    public ProductVariantController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @PostMapping
    public ProductVariant createProductVariant(@RequestBody ProductVariant variant) {
        return productPortfolioService.createProductVariant(variant);
    }

    @GetMapping
    public List<ProductVariant> getAllProductVariants() {
        return productPortfolioService.getAllProductVariants();
    }

    @GetMapping("/{id}")
    public ProductVariant getProductVariantById(@PathVariable String id) {
        return productPortfolioService.getProductVariantById(id);
    }

    @PutMapping("/{id}")
    public ProductVariant updateProductVariant(@PathVariable String id, @RequestBody ProductVariant variant) {
        return productPortfolioService.updateProductVariant(id, variant);
    }

    @DeleteMapping("/{id}")
    public void deleteProductVariant(@PathVariable String id) {
        productPortfolioService.deleteProductVariant(id);
    }

    @PostMapping("/{variantId}/markets/{marketId}")
    public ProductVariant connectVariantToMarket(@PathVariable String variantId, @PathVariable String marketId) {
        return productPortfolioService.connectVariantToMarket(variantId, marketId);
    }

    @DeleteMapping("/{variantId}/markets/{marketId}")
    public ProductVariant removeVariantMarketRelation(@PathVariable String variantId, @PathVariable String marketId) {
        return productPortfolioService.removeVariantMarketRelation(variantId, marketId);
    }
}