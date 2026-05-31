package rs.ac.uns.acs.nais.ProductPortfolioService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Market;
import rs.ac.uns.acs.nais.ProductPortfolioService.service.ProductPortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio/markets")
public class MarketController {

    private final ProductPortfolioService productPortfolioService;

    public MarketController(ProductPortfolioService productPortfolioService) {
        this.productPortfolioService = productPortfolioService;
    }

    @PostMapping
    public Market createMarket(@RequestBody Market market) {
        return productPortfolioService.createMarket(market);
    }

    @GetMapping
    public List<Market> getAllMarkets() {
        return productPortfolioService.getAllMarkets();
    }

    @GetMapping("/{id}")
    public Market getMarketById(@PathVariable String id) {
        return productPortfolioService.getMarketById(id);
    }

    @PutMapping("/{id}")
    public Market updateMarket(@PathVariable String id, @RequestBody Market market) {
        return productPortfolioService.updateMarket(id, market);
    }

    @DeleteMapping("/{id}")
    public void deleteMarket(@PathVariable String id) {
        productPortfolioService.deleteMarket(id);
    }
}