package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.dto.CategorySummaryDto;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Category;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/categories")
public class CategoryController {

    private final ProductPortfolioService productPortfolioService;

    public CategoryController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return productPortfolioService.createCategory(category);
    }

    @GetMapping
    public List<CategorySummaryDto> getAllCategories() {
        return productPortfolioService.getAllCategorySummaries();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable String id) {
        return productPortfolioService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable String id, @RequestBody Category category) {
        return productPortfolioService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        productPortfolioService.deleteCategory(id);
    }

    @PostMapping("/{childCategoryId}/parent/{parentCategoryId}")
    public Category connectSubcategoryToParent(
            @PathVariable String childCategoryId,
            @PathVariable String parentCategoryId
    ) {
        return productPortfolioService.connectSubcategoryToParent(childCategoryId, parentCategoryId);
    }

    @DeleteMapping("/{childCategoryId}/parent/{parentCategoryId}")
    public Category removeSubcategoryRelation(
            @PathVariable String childCategoryId,
            @PathVariable String parentCategoryId
    ) {
        return productPortfolioService.removeSubcategoryRelation(childCategoryId, parentCategoryId);
    }
}