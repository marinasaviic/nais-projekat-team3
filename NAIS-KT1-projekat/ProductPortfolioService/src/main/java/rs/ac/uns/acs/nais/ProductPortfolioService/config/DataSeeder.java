package rs.ac.uns.acs.nais.ProductPortfolioService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.repository.*;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductVariantRepository productVariantRepository,
            MarketRepository marketRepository,
            LifecycleStatusRepository lifecycleStatusRepository
    ) {
        return args -> {
            if (productRepository.count() > 0 || categoryRepository.count() > 0
                    || productVariantRepository.count() > 0 || marketRepository.count() > 0
                    || lifecycleStatusRepository.count() > 0) {
                return;
            }

            // CATEGORIES
            Category dermatology = new Category("category-1", "Dermatologija", "THERAPEUTIC_AREA");
            Category faceCare = new Category("category-2", "Nega lica", "CATEGORY");
            Category painRelief = new Category("category-3", "Analgetici", "THERAPEUTIC_AREA");
            Category gastro = new Category("category-4", "Gastroenterologija", "THERAPEUTIC_AREA");

            categoryRepository.save(dermatology);
            categoryRepository.save(faceCare);
            categoryRepository.save(painRelief);
            categoryRepository.save(gastro);

            categoryRepository.connectSubcategoryToParent("category-2", "category-1");

            // LIFECYCLE STATUSES
            LifecycleStatus development = new LifecycleStatus("status-1", "DEVELOPMENT");
            LifecycleStatus active = new LifecycleStatus("status-2", "ACTIVE");
            LifecycleStatus withdrawn = new LifecycleStatus("status-3", "WITHDRAWN");

            lifecycleStatusRepository.save(development);
            lifecycleStatusRepository.save(active);
            lifecycleStatusRepository.save(withdrawn);

            // MARKETS
            Market serbia = new Market("market-1", "Serbia");
            Market bosnia = new Market("market-2", "Bosnia and Herzegovina");
            Market montenegro = new Market("market-3", "Montenegro");

            marketRepository.save(serbia);
            marketRepository.save(bosnia);
            marketRepository.save(montenegro);

            // PRODUCTS
            Product dermaCare = new Product("product-1", "DermaCare", "DC-001");
            Product painOff = new Product("product-2", "PainOff", "PO-500");
            Product gastroBalance = new Product("product-3", "GastroBalance", "GB-020");
            Product dermaSoft = new Product("product-4", "DermaSoft", "DS-010");

            productRepository.save(dermaCare);
            productRepository.save(painOff);
            productRepository.save(gastroBalance);
            productRepository.save(dermaSoft);

            // PRODUCT VARIANTS
            ProductVariant dermaCare50 = new ProductVariant(
                    "variant-1",
                    "DermaCare 50mg cream",
                    "50mg",
                    "30g",
                    "cream"
            );

            ProductVariant dermaCare100 = new ProductVariant(
                    "variant-2",
                    "DermaCare 100mg cream",
                    "100mg",
                    "50g",
                    "cream"
            );

            ProductVariant painOff500 = new ProductVariant(
                    "variant-3",
                    "PainOff 500mg tablets",
                    "500mg",
                    "20 tablets",
                    "tablet"
            );

            ProductVariant gastroBalance20 = new ProductVariant(
                    "variant-4",
                    "GastroBalance 20mg capsules",
                    "20mg",
                    "30 capsules",
                    "capsule"
            );

            ProductVariant dermaSoft10 = new ProductVariant(
                    "variant-5",
                    "DermaSoft 10mg gel",
                    "10mg",
                    "25g",
                    "gel"
            );

            productVariantRepository.save(dermaCare50);
            productVariantRepository.save(dermaCare100);
            productVariantRepository.save(painOff500);
            productVariantRepository.save(gastroBalance20);
            productVariantRepository.save(dermaSoft10);

            // PRODUCT -> CATEGORY
            productRepository.connectProductToCategory("product-1", "category-2");
            productRepository.connectProductToCategory("product-2", "category-3");
            productRepository.connectProductToCategory("product-3", "category-4");
            productRepository.connectProductToCategory("product-4", "category-2");

            // PRODUCT -> STATUS
            productRepository.connectProductToStatus("product-1", "status-2");
            productRepository.connectProductToStatus("product-2", "status-2");
            productRepository.connectProductToStatus("product-3", "status-1");
            productRepository.connectProductToStatus("product-4", "status-3");

            // PRODUCT -> VARIANTS
            productRepository.connectProductToVariant("product-1", "variant-1");
            productRepository.connectProductToVariant("product-1", "variant-2");
            productRepository.connectProductToVariant("product-2", "variant-3");
            productRepository.connectProductToVariant("product-3", "variant-4");
            productRepository.connectProductToVariant("product-4", "variant-5");

            // VARIANT -> MARKET
            productVariantRepository.connectVariantToMarket("variant-1", "market-1");
            productVariantRepository.connectVariantToMarket("variant-1", "market-2");
            productVariantRepository.connectVariantToMarket("variant-2", "market-1");
            productVariantRepository.connectVariantToMarket("variant-3", "market-1");
            productVariantRepository.connectVariantToMarket("variant-3", "market-3");
            productVariantRepository.connectVariantToMarket("variant-4", "market-2");
            productVariantRepository.connectVariantToMarket("variant-5", "market-3");

                        // Keep the hand-crafted sample data, then generate enough entities to reach exactly 1000.
                        final int targetEntityCount = 1000;
                        final int baseEntityCount = 4 + 3 + 3 + 4 + 5;
                        final int entitiesToGenerate = targetEntityCount - baseEntityCount;

                        int generatedProductCount = entitiesToGenerate / 2;
                        boolean needsExtraVariant = entitiesToGenerate % 2 != 0;

                        String[] categoryIds = {"category-2", "category-3", "category-4"};
                        String[] statusIds = {"status-1", "status-2", "status-3"};
                        String[] marketIds = {"market-1", "market-2", "market-3"};

                        for (int i = 1; i <= generatedProductCount; i++) {
                                String productId = "product-gen-" + i;
                                String variantId = "variant-gen-" + i;

                                Product generatedProduct = new Product(
                                                productId,
                                                "Generated Product " + i,
                                                "GEN-" + String.format("%04d", i)
                                );

                                ProductVariant generatedVariant = new ProductVariant(
                                                variantId,
                                                "Generated Variant " + i,
                                                (10 + (i % 90)) + "mg",
                                                (10 + (i % 40)) + " units",
                                                (i % 2 == 0) ? "tablet" : "capsule"
                                );

                                productRepository.save(generatedProduct);
                                productVariantRepository.save(generatedVariant);

                                productRepository.connectProductToCategory(productId, categoryIds[(i - 1) % categoryIds.length]);
                                productRepository.connectProductToStatus(productId, statusIds[(i - 1) % statusIds.length]);
                                productRepository.connectProductToVariant(productId, variantId);
                                productVariantRepository.connectVariantToMarket(variantId, marketIds[(i - 1) % marketIds.length]);
                        }

                        if (needsExtraVariant) {
                                ProductVariant extraVariant = new ProductVariant(
                                                "variant-gen-extra-1",
                                                "Generated Extra Variant 1",
                                                "25mg",
                                                "15 units",
                                                "tablet"
                                );

                                productVariantRepository.save(extraVariant);
                                productRepository.connectProductToVariant("product-1", "variant-gen-extra-1");
                                productVariantRepository.connectVariantToMarket("variant-gen-extra-1", "market-1");
                        }
        };
    }
}