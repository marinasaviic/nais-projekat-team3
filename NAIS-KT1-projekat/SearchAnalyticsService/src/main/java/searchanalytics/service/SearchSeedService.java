package searchanalytics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchanalytics.model.ProductSearchDocument;
import searchanalytics.model.VariantSearchDocument;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SearchSeedService {
    private final ElasticsearchClientService elasticsearch;
    private final IndexMappingService mappingService;
    private final String productsIndex;
    private final String variantsIndex;

    private final String[] therapeuticAreas = {"Dermatologija", "Analgetici", "Gastroenterologija", "Kardiologija", "Pulmologija"};
    private final String[] categories = {"Nega lica", "Bol i temperatura", "Digestivni trakt", "Krvni pritisak", "Respiratorna terapija"};
    private final String[] subcategories = {"Krema", "Tablete", "Kapsule", "Gel", "Sirup"};
    private final String[] statuses = {"DEVELOPMENT", "ACTIVE", "WITHDRAWN", "ARCHIVED"};
    private final String[] manufacturers = {"PharmaNova", "BalkanPharm", "Medica", "GlobalRx", "BioHealth"};
    private final String[] ingredients = {"Ibuprofen", "Paracetamol", "Omeprazole", "Dexpanthenol", "Salbutamol", "Amlodipine"};
    private final String[] dosageForms = {"tablet", "capsule", "cream", "gel", "syrup"};
    private final String[] markets = {"Serbia", "Bosnia and Herzegovina", "Montenegro", "Croatia", "North Macedonia"};
    private final String[] licenseStatuses = {"ACTIVE", "PENDING_RENEWAL", "EXPIRED", "SUSPENDED"};

    public SearchSeedService(ElasticsearchClientService elasticsearch, IndexMappingService mappingService,
                             @Value("${elasticsearch.products-index}") String productsIndex,
                             @Value("${elasticsearch.variants-index}") String variantsIndex) {
        this.elasticsearch = elasticsearch;
        this.mappingService = mappingService;
        this.productsIndex = productsIndex;
        this.variantsIndex = variantsIndex;
    }

    public String recreateAndSeed(int countPerIndex) throws IOException {
        elasticsearch.deleteIndexIfExists(productsIndex);
        elasticsearch.deleteIndexIfExists(variantsIndex);
        elasticsearch.createIndexIfMissing(productsIndex, mappingService.productMapping());
        elasticsearch.createIndexIfMissing(variantsIndex, mappingService.variantMapping());

        for (int i = 1; i <= countPerIndex; i++) {
            ProductSearchDocument product = buildProduct(i);
            VariantSearchDocument variant = buildVariant(i, product);
            elasticsearch.save(productsIndex, product.getProductId(), product);
            elasticsearch.save(variantsIndex, variant.getVariantId(), variant);
        }
        return "Seeded " + countPerIndex + " documents into " + productsIndex + " and " + countPerIndex + " documents into " + variantsIndex;
    }

    public String ensureSeeded(int countPerIndex) throws IOException {
        try {
            elasticsearch.createIndexIfMissing(productsIndex, mappingService.productMapping());
            elasticsearch.createIndexIfMissing(variantsIndex, mappingService.variantMapping());
            if (elasticsearch.count(productsIndex) >= countPerIndex && elasticsearch.count(variantsIndex) >= countPerIndex) {
                return "Indexes already contain enough documents.";
            }
        } catch (Exception ignored) {
        }
        return recreateAndSeed(countPerIndex);
    }

    private ProductSearchDocument buildProduct(int i) {
        String therapeuticArea = therapeuticAreas[i % therapeuticAreas.length];
        String category = categories[i % categories.length];
        String subcategory = subcategories[i % subcategories.length];
        String status = statuses[i % statuses.length];
        String manufacturer = manufacturers[i % manufacturers.length];
        String ingredient = ingredients[i % ingredients.length];
        String name = ingredient + " Portfolio Product " + i;
        String description = name + " is used for " + therapeuticArea.toLowerCase() + " therapy with focus on safety, market availability, lifecycle monitoring and portfolio optimization.";
        return new ProductSearchDocument(
                "product-search-" + i,
                name,
                "PP-" + String.format("%04d", i),
                description,
                category,
                subcategory,
                therapeuticArea,
                status,
                manufacturer,
                List.of(ingredient.toLowerCase(), therapeuticArea.toLowerCase(), category.toLowerCase()),
                1 + (i % 8),
                1 + (i % markets.length),
                BigDecimal.valueOf(250 + (i % 3000)),
                LocalDate.now().minusDays(i % 900)
        );
    }

    private VariantSearchDocument buildVariant(int i, ProductSearchDocument product) {
        String dosageForm = dosageForms[i % dosageForms.length];
        String ingredient = ingredients[i % ingredients.length];
        return new VariantSearchDocument(
                "variant-search-" + i,
                product.getProductId(),
                product.getName(),
                product.getName() + " " + (10 + (i % 900)) + "mg " + dosageForm,
                dosageForm,
                (10 + (i % 900)) + "mg",
                (10 + (i % 60)) + " units",
                markets[i % markets.length],
                licenseStatuses[i % licenseStatuses.length],
                statuses[(i + 1) % statuses.length],
                BigDecimal.valueOf(150 + (i * 7L % 6000)),
                5 + (i * 13 % 1000),
                ingredient,
                List.of("lactose", "cellulose", "starch"),
                LocalDate.now().minusDays(i % 900)
        );
    }
}
