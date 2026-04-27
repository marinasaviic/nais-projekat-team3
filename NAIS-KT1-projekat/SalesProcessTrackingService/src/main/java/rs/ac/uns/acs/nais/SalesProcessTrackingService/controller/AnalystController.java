package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Analyst;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.AnalystService;

import java.util.List;

@RestController
@RequestMapping("/analysts")
public class AnalystController {

    @Autowired
    private AnalystService analystService;

    @PostMapping
    public Analyst create(@RequestBody Analyst analyst) {
        return analystService.save(analyst);
    }

    @GetMapping
    public List<Analyst> getAll() {
        return analystService.findAll();
    }

    @PutMapping("/{id}")
    public Analyst update(@PathVariable Long id, @RequestBody Analyst analyst) {
        return analystService.update(id, analyst);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        analystService.delete(id);
    }

    @PostMapping("/{analystId}/reports/{reportId}")
    public Analyst connectToReport(@PathVariable Long analystId,
                                   @PathVariable Long reportId,
                                   @RequestParam String causalityAssessment,
                                   @RequestParam String note) {
        return analystService.connectAnalystToReport(analystId, reportId, causalityAssessment, note);
    }
}