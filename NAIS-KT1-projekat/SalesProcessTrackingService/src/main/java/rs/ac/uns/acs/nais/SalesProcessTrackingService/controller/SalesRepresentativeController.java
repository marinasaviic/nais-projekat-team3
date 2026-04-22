package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesRepresentative;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.GraphSalesService;

import java.util.List;

@RestController
@RequestMapping("/api/representatives")
public class SalesRepresentativeController {

    private final GraphSalesService graphSalesService;

    public SalesRepresentativeController(GraphSalesService graphSalesService) {
        this.graphSalesService = graphSalesService;
    }

    @PostMapping
    public SalesRepresentative createSalesRepresentative(@RequestBody SalesRepresentative representative) {
        return graphSalesService.createSalesRepresentative(representative);
    }

    @GetMapping
    public List<SalesRepresentative> getAllSalesRepresentatives() {
        return graphSalesService.getAllSalesRepresentatives();
    }

    @GetMapping("/{id}")
    public SalesRepresentative getSalesRepresentativeById(@PathVariable String id) {
        return graphSalesService.getSalesRepresentativeById(id);
    }

    @PutMapping("/{id}")
    public SalesRepresentative updateSalesRepresentative(@PathVariable String id, @RequestBody SalesRepresentative representative) {
        return graphSalesService.updateSalesRepresentative(id, representative);
    }

    @DeleteMapping("/{id}")
    public void deleteSalesRepresentative(@PathVariable String id) {
        graphSalesService.deleteSalesRepresentative(id);
    }

    @PostMapping("/{representativeId}/processes/{processId}")
    public void connectRepresentativeToProcess(@PathVariable String representativeId, @PathVariable String processId) {
        graphSalesService.connectRepresentativeToProcess(representativeId, processId);
    }

    @DeleteMapping("/{representativeId}/processes/{processId}")
    public void removeRepresentativeProcessRelation(@PathVariable String representativeId, @PathVariable String processId) {
        graphSalesService.removeRepresentativeProcessRelation(representativeId, processId);
    }
}