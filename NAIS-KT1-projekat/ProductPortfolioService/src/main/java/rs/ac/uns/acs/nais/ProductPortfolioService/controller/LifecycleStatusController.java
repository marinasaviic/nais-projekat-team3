package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.LifecycleStatus;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/statuses")
public class LifecycleStatusController {

    private final ProductPortfolioService productPortfolioService;

    public LifecycleStatusController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @PostMapping
    public LifecycleStatus createLifecycleStatus(@RequestBody LifecycleStatus status) {
        return productPortfolioService.createLifecycleStatus(status);
    }

    @GetMapping
    public List<LifecycleStatus> getAllLifecycleStatuses() {
        return productPortfolioService.getAllLifecycleStatuses();
    }

    @GetMapping("/{id}")
    public LifecycleStatus getLifecycleStatusById(@PathVariable String id) {
        return productPortfolioService.getLifecycleStatusById(id);
    }

    @PutMapping("/{id}")
    public LifecycleStatus updateLifecycleStatus(@PathVariable String id, @RequestBody LifecycleStatus status) {
        return productPortfolioService.updateLifecycleStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteLifecycleStatus(@PathVariable String id) {
        productPortfolioService.deleteLifecycleStatus(id);
    }
}