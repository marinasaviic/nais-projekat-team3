package rs.ac.uns.acs.nais.ProductPortfolioService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ProductPortfolioService.dto.CategorySummaryDto;
import rs.ac.uns.acs.nais.ProductPortfolioService.dto.ProductSummaryDto;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.repository.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductPortfolioService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final MarketRepository marketRepository;
    private final LifecycleStatusRepository lifecycleStatusRepository;

    public ProductPortfolioService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductVariantRepository productVariantRepository,
            MarketRepository marketRepository,
            LifecycleStatusRepository lifecycleStatusRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.marketRepository = marketRepository;
        this.lifecycleStatusRepository = lifecycleStatusRepository;
    }

    // PRODUCT CRUD

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductSummaryDto> getAllProductSummaries() {
        return productRepository.findAll().stream()
                .map(product -> new ProductSummaryDto(
                        product.getId(),
                        product.getName(),
                        product.getCode(),
                        product.getCategory() != null ? product.getCategory().getId() : null,
                        product.getCategory() != null ? product.getCategory().getName() : null,
                        product.getLifecycleStatus() != null ? product.getLifecycleStatus().getId() : null,
                        product.getLifecycleStatus() != null ? product.getLifecycleStatus().getName() : null
                ))
                .collect(Collectors.toList());
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    public Product updateProduct(String id, Product updatedProduct) {
        Product product = getProductById(id);
        product.setName(updatedProduct.getName());
        product.setCode(updatedProduct.getCode());
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    // CATEGORY CRUD

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategorySummaryDto> getAllCategorySummaries() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategorySummaryDto(
                        category.getId(),
                        category.getName(),
                        category.getLevel(),
                        category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                        category.getParentCategory() != null ? category.getParentCategory().getName() : null,
                        category.getSubcategories().stream()
                                .map(Category::getId)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }

    public Category updateCategory(String id, Category updatedCategory) {
        Category category = getCategoryById(id);
        category.setName(updatedCategory.getName());
        category.setLevel(updatedCategory.getLevel());
        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    // PRODUCT VARIANT CRUD

    public ProductVariant createProductVariant(ProductVariant variant) {
        return productVariantRepository.save(variant);
    }

    public List<ProductVariant> getAllProductVariants() {
        return productVariantRepository.findAll();
    }

    public ProductVariant getProductVariantById(String id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product variant not found with id: " + id));
    }

    public ProductVariant updateProductVariant(String id, ProductVariant updatedVariant) {
        ProductVariant variant = getProductVariantById(id);
        variant.setName(updatedVariant.getName());
        variant.setStrength(updatedVariant.getStrength());
        variant.setPackageSize(updatedVariant.getPackageSize());
        variant.setForm(updatedVariant.getForm());
        return productVariantRepository.save(variant);
    }

    public void deleteProductVariant(String id) {
        productVariantRepository.deleteById(id);
    }

    // MARKET CRUD

    public Market createMarket(Market market) {
        return marketRepository.save(market);
    }

    public List<Market> getAllMarkets() {
        return marketRepository.findAll();
    }

    public Market getMarketById(String id) {
        return marketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Market not found with id: " + id));
    }

    public Market updateMarket(String id, Market updatedMarket) {
        Market market = getMarketById(id);
        market.setCountryName(updatedMarket.getCountryName());
        return marketRepository.save(market);
    }

    public void deleteMarket(String id) {
        marketRepository.deleteById(id);
    }

    // LIFECYCLE STATUS CRUD

    public LifecycleStatus createLifecycleStatus(LifecycleStatus status) {
        return lifecycleStatusRepository.save(status);
    }

    public List<LifecycleStatus> getAllLifecycleStatuses() {
        return lifecycleStatusRepository.findAll();
    }

    public LifecycleStatus getLifecycleStatusById(String id) {
        return lifecycleStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lifecycle status not found with id: " + id));
    }

    public LifecycleStatus updateLifecycleStatus(String id, LifecycleStatus updatedStatus) {
        LifecycleStatus status = getLifecycleStatusById(id);
        status.setName(updatedStatus.getName());
        return lifecycleStatusRepository.save(status);
    }

    public void deleteLifecycleStatus(String id) {
        lifecycleStatusRepository.deleteById(id);
    }

    // RELATIONSHIPS

    public Product connectProductToCategory(String productId, String categoryId) {
        getProductById(productId);
        getCategoryById(categoryId);
        return productRepository.connectProductToCategory(productId, categoryId);
    }

    public Product removeProductCategoryRelation(String productId) {
        getProductById(productId);
        return productRepository.removeProductCategoryRelation(productId);
    }

    public Product connectProductToVariant(String productId, String variantId) {
        getProductById(productId);
        getProductVariantById(variantId);
        return productRepository.connectProductToVariant(productId, variantId);
    }

    public Product removeProductVariantRelation(String productId, String variantId) {
        getProductById(productId);
        getProductVariantById(variantId);
        return productRepository.removeProductVariantRelation(productId, variantId);
    }

    public Product connectProductToStatus(String productId, String statusId) {
        getProductById(productId);
        getLifecycleStatusById(statusId);

        try {
            productRepository.removeProductStatusRelation(productId);
        } catch (Exception ignored) {
        }

        return productRepository.connectProductToStatus(productId, statusId);
    }

    public Product removeProductStatusRelation(String productId) {
        getProductById(productId);
        return productRepository.removeProductStatusRelation(productId);
    }

    public ProductVariant connectVariantToMarket(String variantId, String marketId) {
        getProductVariantById(variantId);
        getMarketById(marketId);
        return productVariantRepository.connectVariantToMarket(variantId, marketId);
    }

    public ProductVariant removeVariantMarketRelation(String variantId, String marketId) {
        getProductVariantById(variantId);
        getMarketById(marketId);
        return productVariantRepository.removeVariantMarketRelation(variantId, marketId);
    }

    public Category connectSubcategoryToParent(String childCategoryId, String parentCategoryId) {
        getCategoryById(childCategoryId);
        getCategoryById(parentCategoryId);
        return categoryRepository.connectSubcategoryToParent(childCategoryId, parentCategoryId);
    }

    public Category removeSubcategoryRelation(String childCategoryId, String parentCategoryId) {
        getCategoryById(childCategoryId);
        getCategoryById(parentCategoryId);
        return categoryRepository.removeSubcategoryRelation(childCategoryId, parentCategoryId);
    }

    // QUERIES

    public List<String> countProductsByCategory() {
        return productRepository.countProductsByCategory();
    }

    public List<String> countProductsByStatus() {
        return productRepository.countProductsByStatus();
    }

    public List<String> countVariantsByMarket() {
        return productVariantRepository.countVariantsByMarket();
    }

    public List<String> findProductsWithMultipleVariants() {
        return productRepository.findProductsWithMultipleVariants();
    }

    public List<String> countActiveProductsByCategoryAndMarket() {
        return productRepository.countActiveProductsByCategoryAndMarket();
    }
}