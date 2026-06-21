package searchanalytics.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IndexMappingService {
    public Map<String, Object> productMapping() {
        return Map.of("mappings", Map.of("properties", Map.ofEntries(
                Map.entry("productId", Map.of("type", "keyword")),
                Map.entry("name", Map.of("type", "text", "fields", Map.of("keyword", Map.of("type", "keyword")))),
                Map.entry("code", Map.of("type", "keyword")),
                Map.entry("description", Map.of("type", "text")),
                Map.entry("category", Map.of("type", "keyword")),
                Map.entry("subcategory", Map.of("type", "keyword")),
                Map.entry("therapeuticArea", Map.of("type", "keyword")),
                Map.entry("lifecycleStatus", Map.of("type", "keyword")),
                Map.entry("manufacturer", Map.of("type", "keyword")),
                Map.entry("tags", Map.of("type", "keyword")),
                Map.entry("numberOfVariants", Map.of("type", "integer")),
                Map.entry("marketsCount", Map.of("type", "integer")),
                Map.entry("averageVariantPrice", Map.of("type", "double")),
                Map.entry("createdAt", Map.of("type", "date"))
        )));
    }

    public Map<String, Object> variantMapping() {
        return Map.of("mappings", Map.of("properties", Map.ofEntries(
                Map.entry("variantId", Map.of("type", "keyword")),
                Map.entry("productId", Map.of("type", "keyword")),
                Map.entry("productName", Map.of("type", "text", "fields", Map.of("keyword", Map.of("type", "keyword")))),
                Map.entry("name", Map.of("type", "text", "fields", Map.of("keyword", Map.of("type", "keyword")))),
                Map.entry("dosageForm", Map.of("type", "keyword")),
                Map.entry("strength", Map.of("type", "keyword")),
                Map.entry("packageSize", Map.of("type", "keyword")),
                Map.entry("market", Map.of("type", "keyword")),
                Map.entry("licenseStatus", Map.of("type", "keyword")),
                Map.entry("lifecycleStatus", Map.of("type", "keyword")),
                Map.entry("price", Map.of("type", "double")),
                Map.entry("availableQuantity", Map.of("type", "integer")),
                Map.entry("activeIngredient", Map.of("type", "keyword")),
                Map.entry("excipients", Map.of("type", "keyword")),
                Map.entry("createdAt", Map.of("type", "date"))
        )));
    }
}
