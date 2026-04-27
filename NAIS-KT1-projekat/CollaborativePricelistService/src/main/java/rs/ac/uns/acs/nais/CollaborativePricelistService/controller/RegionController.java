package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Region;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final CollaborationGraphService collaborationGraphService;

    public RegionController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @PostMapping
    public Region createRegion(@RequestBody Region region) {
        return collaborationGraphService.createRegion(region);
    }

    @GetMapping
    public List<Region> getAllRegions() {
        return collaborationGraphService.getAllRegions();
    }

    @GetMapping("/{id}")
    public Region getRegionById(@PathVariable String id) {
        return collaborationGraphService.getRegionById(id);
    }

    @PutMapping("/{id}")
    public Region updateRegion(@PathVariable String id, @RequestBody Region region) {
        return collaborationGraphService.updateRegion(id, region);
    }

    @DeleteMapping("/{id}")
    public void deleteRegion(@PathVariable String id) {
        collaborationGraphService.deleteRegion(id);
    }
}
