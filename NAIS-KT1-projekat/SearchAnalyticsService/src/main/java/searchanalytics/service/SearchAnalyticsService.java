package searchanalytics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import searchanalytics.dto.*;
import searchanalytics.model.ProductSearchDocument;
import searchanalytics.model.VariantSearchDocument;

import java.io.IOException;
import java.util.*;

@Service
public class SearchAnalyticsService {
    private final ElasticsearchClientService elasticsearch;
    private final String productsIndex;
    private final String variantsIndex;

    public SearchAnalyticsService(ElasticsearchClientService elasticsearch,
                                  @Value("${elasticsearch.products-index}") String productsIndex,
                                  @Value("${elasticsearch.variants-index}") String variantsIndex) {
        this.elasticsearch = elasticsearch;
        this.productsIndex = productsIndex;
        this.variantsIndex = variantsIndex;
    }

    @CachePut(value = "products", key = "#document.productId")
    public ProductSearchDocument saveProduct(ProductSearchDocument document) throws IOException {
        elasticsearch.save(productsIndex, document.getProductId(), document);
        return document;
    }

    @Cacheable(value = "products", key = "#id")
    public Map<String, Object> getProduct(String id) throws IOException {
        return elasticsearch.getById(productsIndex, id);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(String id) throws IOException {
        elasticsearch.delete(productsIndex, id);
    }

    @CachePut(value = "variants", key = "#document.variantId")
    public VariantSearchDocument saveVariant(VariantSearchDocument document) throws IOException {
        elasticsearch.save(variantsIndex, document.getVariantId(), document);
        return document;
    }

    @Cacheable(value = "variants", key = "#id")
    public Map<String, Object> getVariant(String id) throws IOException {
        return elasticsearch.getById(variantsIndex, id);
    }

    @CacheEvict(value = "variants", key = "#id")
    public void deleteVariant(String id) throws IOException {
        elasticsearch.delete(variantsIndex, id);
    }

    
    public SearchResponse advancedProductSearch(AdvancedProductSearchRequest request) throws IOException {
        List<Object> must = new ArrayList<>();
        List<Object> filter = new ArrayList<>();

        if (hasText(request.getText())) {
            must.add(Map.of("multi_match", Map.of(
                    "query", request.getText(),
                    "fields", List.of("name^3", "description^2", "tags"),
                    "operator", "and"
            )));
        } else {
            must.add(Map.of("match_all", Map.of()));
        }
        addTerm(filter, "lifecycleStatus", request.getLifecycleStatus());
        addTerm(filter, "therapeuticArea", request.getTherapeuticArea());
        addTerm(filter, "category", request.getCategory());
        addTerm(filter, "manufacturer", request.getManufacturer());
        addGte(filter, "numberOfVariants", request.getMinVariants());
        addGte(filter, "marketsCount", request.getMinMarkets());

        Map<String, Object> body = Map.of(
                "track_total_hits", true,
                "from", request.getPage() * request.getSize(),
                "size", request.getSize(),
                "query", Map.of("bool", Map.of("must", must, "filter", filter)),
                "sort", List.of(Map.of(safeSort(request.getSortBy(), "numberOfVariants"), Map.of("order", safeDirection(request.getSortDirection())))),
                "aggs", Map.of(
                        "by_therapeutic_area", Map.of("terms", Map.of("field", "therapeuticArea", "size", 10)),
                        "by_category", Map.of("terms", Map.of("field", "category", "size", 10)),
                        "by_status", Map.of("terms", Map.of("field", "lifecycleStatus", "size", 10)),
                        "avg_variant_price", Map.of("avg", Map.of("field", "averageVariantPrice")),
                        "max_markets_count", Map.of("max", Map.of("field", "marketsCount"))
                )
        );
        return elasticsearch.search(productsIndex, body);
    }

    public SearchResponse marketAvailabilitySearch(MarketAvailabilitySearchRequest request) throws IOException {
        List<Object> filter = new ArrayList<>();
        addTerm(filter, "market", request.getMarket());
        addTerm(filter, "dosageForm", request.getDosageForm());
        addTerm(filter, "lifecycleStatus", request.getLifecycleStatus());
        addTerm(filter, "licenseStatus", request.getLicenseStatus());
        addRange(filter, "price", request.getMinPrice(), request.getMaxPrice());
        addGte(filter, "availableQuantity", request.getMinAvailableQuantity());

        Map<String, Object> body = Map.of(
                "track_total_hits", true,
                "from", request.getPage() * request.getSize(),
                "size", request.getSize(),
                "query", Map.of("bool", Map.of("filter", filter)),
                "sort", List.of(Map.of(safeSort(request.getSortBy(), "price"), Map.of("order", safeDirection(request.getSortDirection())))),
                "aggs", Map.of(
                        "by_market", Map.of("terms", Map.of("field", "market", "size", 10)),
                        "by_dosage_form", Map.of("terms", Map.of("field", "dosageForm", "size", 10)),
                        "by_license_status", Map.of("terms", Map.of("field", "licenseStatus", "size", 10)),
                        "avg_price", Map.of("avg", Map.of("field", "price")),
                        "total_available_quantity", Map.of("sum", Map.of("field", "availableQuantity"))
                )
        );
        return elasticsearch.search(variantsIndex, body);
    }

    public SearchResponse portfolioRiskSearch(PortfolioRiskSearchRequest request) throws IOException {
    List<Object> must = new ArrayList<>();
    List<Object> filter = new ArrayList<>();

    if (hasText(request.getText())) {
        must.add(Map.of("multi_match", Map.of(
                "query", request.getText(),
                "fields", List.of("name^3", "productName^2", "activeIngredient"),
                "operator", "and"
        )));
    } else {
        must.add(Map.of("match_all", Map.of()));
    }

    addTerm(filter, "market", request.getMarket());
    addTerm(filter, "dosageForm", request.getDosageForm());
    addTerm(filter, "lifecycleStatus", request.getLifecycleStatus());
    addTerm(filter, "licenseStatus", request.getLicenseStatus());
    addTerm(filter, "activeIngredient", request.getActiveIngredient());
    addLte(filter, "availableQuantity", request.getMaxAvailableQuantity());
    addRange(filter, "price", request.getMinPrice(), request.getMaxPrice());

    Map<String, Object> body = Map.of(
            "track_total_hits", true,
            "from", request.getPage() * request.getSize(),
            "size", request.getSize(),
            "query", Map.of("bool", Map.of("must", must, "filter", filter)),
            "sort", List.of(
                    Map.of(safeSort(request.getSortBy(), "availableQuantity"), Map.of("order", safeDirection(request.getSortDirection()))),
                    Map.of("price", Map.of("order", "desc"))
            ),
            "aggs", Map.of(
                    "risk_by_market", Map.of(
                            "terms", Map.of("field", "market", "size", 10),
                            "aggs", Map.of(
                                    "avg_price", Map.of("avg", Map.of("field", "price")),
                                    "total_available_quantity", Map.of("sum", Map.of("field", "availableQuantity")),
                                    "min_available_quantity", Map.of("min", Map.of("field", "availableQuantity"))
                            )
                    ),
                    "risk_by_license_status", Map.of("terms", Map.of("field", "licenseStatus", "size", 10)),
                    "risk_by_lifecycle_status", Map.of("terms", Map.of("field", "lifecycleStatus", "size", 10)),
                    "risk_by_dosage_form", Map.of("terms", Map.of("field", "dosageForm", "size", 10)),
                    "avg_risky_variant_price", Map.of("avg", Map.of("field", "price")),
                    "low_stock_distribution", Map.of("histogram", Map.of("field", "availableQuantity", "interval", 25))
            )
    );

    return elasticsearch.search(variantsIndex, body);
    }


    public SearchResponse getAllProducts(int page, int size) throws IOException {
        return elasticsearch.search(productsIndex, Map.of("track_total_hits", true, "from", page * size, "size", size, "query", Map.of("match_all", Map.of())));
    }

    public SearchResponse getAllVariants(int page, int size) throws IOException {
        return elasticsearch.search(variantsIndex, Map.of("track_total_hits", true, "from", page * size, "size", size, "query", Map.of("match_all", Map.of())));
    }

    private void addTerm(List<Object> filter, String field, String value) {
        if (hasText(value)) filter.add(Map.of("term", Map.of(field, value)));
    }

    private void addGte(List<Object> filter, String field, Object value) {
        if (value != null) filter.add(Map.of("range", Map.of(field, Map.of("gte", value))));
    }

    private void addLte(List<Object> filter, String field, Object value) {
        if (value != null) filter.add(Map.of("range", Map.of(field, Map.of("lte", value))));
    }

    private void addRange(List<Object> filter, String field, Object min, Object max) {
        Map<String, Object> range = new LinkedHashMap<>();
        if (min != null) range.put("gte", min);
        if (max != null) range.put("lte", max);
        if (!range.isEmpty()) filter.add(Map.of("range", Map.of(field, range)));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String safeDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) ? "asc" : "desc";
    }

    private String safeSort(String sortBy, String fallback) {
        if (!hasText(sortBy)) return fallback;
        return sortBy;
    }
}
