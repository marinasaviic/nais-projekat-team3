package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesProcess;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.GraphSalesService;

import org.springframework.http.ResponseEntity;
import java.util.Map;
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
    public ResponseEntity<?> setCurrentStage(@PathVariable String processId, @PathVariable String stageId) {
        try {
            return ResponseEntity.ok(graphSalesService.setCurrentStage(processId, stageId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(409).body(Map.of(
                    "message", ex.getMessage()
            ));
        }
    }

    @DeleteMapping("/{processId}/stage")
    public void removeCurrentStageRelation(@PathVariable String processId) {
        graphSalesService.removeCurrentStageRelation(processId);
    }
}