package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.dto.ProductSummaryDto;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Product;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/products")
public class ProductController {

    private final ProductPortfolioService productPortfolioService;

    public ProductController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productPortfolioService.createProduct(product);
    }

    @GetMapping
    public List<ProductSummaryDto> getAllProducts() {
        return productPortfolioService.getAllProductSummaries();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productPortfolioService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productPortfolioService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productPortfolioService.deleteProduct(id);
    }

    @PostMapping("/{productId}/category/{categoryId}")
    public Product connectProductToCategory(@PathVariable String productId, @PathVariable String categoryId) {
        return productPortfolioService.connectProductToCategory(productId, categoryId);
    }

    @DeleteMapping("/{productId}/category")
    public Product removeProductCategoryRelation(@PathVariable String productId) {
        return productPortfolioService.removeProductCategoryRelation(productId);
    }

    @PostMapping("/{productId}/variants/{variantId}")
    public Product connectProductToVariant(@PathVariable String productId, @PathVariable String variantId) {
        return productPortfolioService.connectProductToVariant(productId, variantId);
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    public Product removeProductVariantRelation(@PathVariable String productId, @PathVariable String variantId) {
        return productPortfolioService.removeProductVariantRelation(productId, variantId);
    }

    @PostMapping("/{productId}/status/{statusId}")
    public Product connectProductToStatus(@PathVariable String productId, @PathVariable String statusId) {
        return productPortfolioService.connectProductToStatus(productId, statusId);
    }

    @DeleteMapping("/{productId}/status")
    public Product removeProductStatusRelation(@PathVariable String productId) {
        return productPortfolioService.removeProductStatusRelation(productId);
    }
}