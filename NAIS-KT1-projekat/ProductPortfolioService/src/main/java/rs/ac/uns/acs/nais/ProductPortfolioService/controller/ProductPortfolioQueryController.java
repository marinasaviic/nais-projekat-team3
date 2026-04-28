package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/queries")
public class ProductPortfolioQueryController {

    private final ProductPortfolioService productPortfolioService;

    public ProductPortfolioQueryController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @GetMapping("/product-count-by-category")
    public List<String> countProductsByCategory() {
        return productPortfolioService.countProductsByCategory();
    }

    @GetMapping("/product-count-by-status")
    public List<String> countProductsByStatus() {
        return productPortfolioService.countProductsByStatus();
    }

    @GetMapping("/variant-count-by-market")
    public List<String> countVariantsByMarket() {
        return productPortfolioService.countVariantsByMarket();
    }

    @GetMapping("/products-with-multiple-variants")
    public List<String> findProductsWithMultipleVariants() {
        return productPortfolioService.findProductsWithMultipleVariants();
    }

    @GetMapping("/active-products-by-category-and-market")
    public List<String> countActiveProductsByCategoryAndMarket() {
        return productPortfolioService.countActiveProductsByCategoryAndMarket();
    }
}