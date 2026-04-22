package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesProcess;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.GraphSalesService;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class SalesProcessController {

    private final GraphSalesService graphSalesService;

    public SalesProcessController(GraphSalesService graphSalesService) {
        this.graphSalesService = graphSalesService;
    }

    @PostMapping
    public SalesProcess createSalesProcess(@RequestBody SalesProcess salesProcess) {
        return graphSalesService.createSalesProcess(salesProcess);
    }

    @GetMapping
    public List<SalesProcess> getAllSalesProcesses() {
        return graphSalesService.getAllSalesProcesses();
    }

    @GetMapping("/{id}")
    public SalesProcess getSalesProcessById(@PathVariable String id) {
        return graphSalesService.getSalesProcessById(id);
    }

    @PutMapping("/{id}")
    public SalesProcess updateSalesProcess(@PathVariable String id, @RequestBody SalesProcess salesProcess) {
        return graphSalesService.updateSalesProcess(id, salesProcess);
    }

    @DeleteMapping("/{id}")
    public void deleteSalesProcess(@PathVariable String id) {
        graphSalesService.deleteSalesProcess(id);
    }

    @PostMapping("/{processId}/stage/{stageId}")
    public void setCurrentStage(@PathVariable String processId, @PathVariable String stageId) {
        graphSalesService.setCurrentStage(processId, stageId);
    }

    @DeleteMapping("/{processId}/stage")
    public void removeCurrentStageRelation(@PathVariable String processId) {
        graphSalesService.removeCurrentStageRelation(processId);
    }
}