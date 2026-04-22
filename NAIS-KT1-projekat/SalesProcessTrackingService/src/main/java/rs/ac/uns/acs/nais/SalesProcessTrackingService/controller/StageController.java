package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Stage;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.GraphSalesService;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    private final GraphSalesService graphSalesService;

    public StageController(GraphSalesService graphSalesService) {
        this.graphSalesService = graphSalesService;
    }

    @PostMapping
    public Stage createStage(@RequestBody Stage stage) {
        return graphSalesService.createStage(stage);
    }

    @GetMapping
    public List<Stage> getAllStages() {
        return graphSalesService.getAllStages();
    }

    @GetMapping("/{id}")
    public Stage getStageById(@PathVariable String id) {
        return graphSalesService.getStageById(id);
    }

    @PutMapping("/{id}")
    public Stage updateStage(@PathVariable String id, @RequestBody Stage stage) {
        return graphSalesService.updateStage(id, stage);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(@PathVariable String id) {
        graphSalesService.deleteStage(id);
    }

    @PostMapping("/{fromStageId}/next/{toStageId}")
    public Stage connectStageToStage(@PathVariable String fromStageId, @PathVariable String toStageId) {
        return graphSalesService.connectStageToStage(fromStageId, toStageId);
    }

    @DeleteMapping("/{fromStageId}/next/{toStageId}")
    public Stage removeStageToStageRelation(@PathVariable String fromStageId, @PathVariable String toStageId) {
        return graphSalesService.removeStageToStageRelation(fromStageId, toStageId);
    }
}