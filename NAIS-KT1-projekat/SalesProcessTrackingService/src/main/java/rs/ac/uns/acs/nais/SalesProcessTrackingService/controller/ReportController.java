package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Report;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public Report create(@RequestBody Report report) {
        return reportService.save(report);
    }

    @GetMapping
    public List<Report> getAll() {
        return reportService.findAll();
    }

    @PutMapping("/{id}")
    public Report update(@PathVariable Long id, @RequestBody Report report) {
        return reportService.update(id, report);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reportService.delete(id);
    }

    @PostMapping("/{reportId}/medication/{medicationId}")
    public Report connectToMedication(@PathVariable Long reportId,
                                      @PathVariable Long medicationId) {
        return reportService.connectReportToMedication(reportId, medicationId);
    }

    @PostMapping("/{reportId}/side-effect/{sideEffectId}")
    public Report connectToSideEffect(@PathVariable Long reportId,
                                      @PathVariable Long sideEffectId) {
        return reportService.connectReportToSideEffect(reportId, sideEffectId);
    }
}