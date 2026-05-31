package searchanalytics.controller;

import org.springframework.web.bind.annotation.*;
import searchanalytics.dto.*;
import searchanalytics.model.ProductSearchDocument;
import searchanalytics.model.VariantSearchDocument;
import searchanalytics.service.SearchAnalyticsService;
import searchanalytics.service.SearchSeedService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchAnalyticsController {
    private final SearchAnalyticsService searchAnalyticsService;
    private final SearchSeedService seedService;

    public SearchAnalyticsController(SearchAnalyticsService searchAnalyticsService, SearchSeedService seedService) {
        this.searchAnalyticsService = searchAnalyticsService;
        this.seedService = seedService;
    }

    @PostMapping("/seed")
    public String seed(@RequestParam(defaultValue = "1200") int countPerIndex,
                       @RequestParam(defaultValue = "false") boolean recreate) throws IOException {
        return recreate ? seedService.recreateAndSeed(countPerIndex) : seedService.ensureSeeded(countPerIndex);
    }

    @PostMapping("/products")
    public ProductSearchDocument createProduct(@RequestBody ProductSearchDocument document) throws IOException {
        return searchAnalyticsService.saveProduct(document);
    }

    @GetMapping("/products")
    public SearchResponse getProducts(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) throws IOException {
        return searchAnalyticsService.getAllProducts(page, size);
    }

    @GetMapping("/products/{id}")
    public Map<String, Object> getProduct(@PathVariable String id) throws IOException {
        return searchAnalyticsService.getProduct(id);
    }

    @PutMapping("/products/{id}")
    public ProductSearchDocument updateProduct(@PathVariable String id, @RequestBody ProductSearchDocument document) throws IOException {
        document.setProductId(id);
        return searchAnalyticsService.saveProduct(document);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable String id) throws IOException {
        searchAnalyticsService.deleteProduct(id);
    }

    @PostMapping("/variants")
    public VariantSearchDocument createVariant(@RequestBody VariantSearchDocument document) throws IOException {
        return searchAnalyticsService.saveVariant(document);
    }

    @GetMapping("/variants")
    public SearchResponse getVariants(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) throws IOException {
        return searchAnalyticsService.getAllVariants(page, size);
    }

    @GetMapping("/variants/{id}")
    public Map<String, Object> getVariant(@PathVariable String id) throws IOException {
        return searchAnalyticsService.getVariant(id);
    }

    @PutMapping("/variants/{id}")
    public VariantSearchDocument updateVariant(@PathVariable String id, @RequestBody VariantSearchDocument document) throws IOException {
        document.setVariantId(id);
        return searchAnalyticsService.saveVariant(document);
    }

    @DeleteMapping("/variants/{id}")
    public void deleteVariant(@PathVariable String id) throws IOException {
        searchAnalyticsService.deleteVariant(id);
    }

    @PostMapping("/products/advanced")
    public SearchResponse advancedProductSearch(@RequestBody AdvancedProductSearchRequest request) throws IOException {
        return searchAnalyticsService.advancedProductSearch(request);
    }

    @PostMapping("/variants/market-availability")
    public SearchResponse marketAvailabilitySearch(@RequestBody MarketAvailabilitySearchRequest request) throws IOException {
        return searchAnalyticsService.marketAvailabilitySearch(request);
    }

    @PostMapping("/portfolio/risk-analysis")
    public SearchResponse portfolioRiskSearch(@RequestBody PortfolioRiskSearchRequest request) throws IOException {
        return searchAnalyticsService.portfolioRiskSearch(request);
    }
}
