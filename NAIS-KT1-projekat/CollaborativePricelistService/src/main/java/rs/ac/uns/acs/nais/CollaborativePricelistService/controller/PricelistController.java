package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/pricelists")
public class PricelistController {

    private final CollaborationGraphService collaborationGraphService;

    public PricelistController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @PostMapping
    public Pricelist createPricelist(@RequestBody Pricelist pricelist) {
        return collaborationGraphService.createPricelist(pricelist);
    }

    @GetMapping
    public List<Pricelist> getAllPricelists() {
        return collaborationGraphService.getAllPricelists();
    }

    @GetMapping("/{id}")
    public Pricelist getPricelistById(@PathVariable String id) {
        return collaborationGraphService.getPricelistById(id);
    }

    @PutMapping("/{id}")
    public Pricelist updatePricelist(@PathVariable String id, @RequestBody Pricelist pricelist) {
        return collaborationGraphService.updatePricelist(id, pricelist);
    }

    @DeleteMapping("/{id}")
    public void deletePricelist(@PathVariable String id) {
        collaborationGraphService.deletePricelist(id);
    }
}
