package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.GraphSalesService;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
public class GraphQueryController {

    private final GraphSalesService graphSalesService;

    public GraphQueryController(GraphSalesService graphSalesService) {
        this.graphSalesService = graphSalesService;
    }

    @GetMapping("/process-count-by-stage")
    public List<String> countProcessesByStage() {
        return graphSalesService.countProcessesByStage();
    }

    @GetMapping("/process-count-by-representative")
    public List<String> countProcessesByRepresentative() {
        return graphSalesService.countProcessesByRepresentative();
    }

    @GetMapping("/customers-with-multiple-processes")
    public List<String> findCustomersWithMultipleProcesses() {
        return graphSalesService.findCustomersWithMultipleProcesses();
    }

    @GetMapping("/allowed-transitions/{stageName}")
    public List<String> findAllowedTransitions(@PathVariable String stageName) {
        return graphSalesService.findAllowedTransitions(stageName);
    }

    @GetMapping("/process-count-by-status-and-stage")
    public List<String> countProcessesByStatusAndStage() {
        return graphSalesService.countProcessesByStatusAndStage();
    }
}