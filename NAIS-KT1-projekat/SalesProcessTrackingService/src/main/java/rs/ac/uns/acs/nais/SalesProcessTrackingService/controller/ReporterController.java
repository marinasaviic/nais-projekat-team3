package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Reporter;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.ReporterService;

import java.util.List;

@RestController
@RequestMapping("/reporters")
public class ReporterController {

    @Autowired
    private ReporterService reporterService;

    @PostMapping
    public Reporter create(@RequestBody Reporter reporter) {
        return reporterService.save(reporter);
    }

    @GetMapping
    public List<Reporter> getAll() {
        return reporterService.findAll();
    }

    @PutMapping("/{id}")
    public Reporter update(@PathVariable Long id, @RequestBody Reporter reporter) {
        return reporterService.update(id, reporter);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reporterService.delete(id);
    }

    @PostMapping("/{reporterId}/reports/{reportId}")
    public Reporter connectToReport(@PathVariable Long reporterId,
                                    @PathVariable Long reportId) {
        return reporterService.connectReporterToReport(reporterId, reportId);
    }
}