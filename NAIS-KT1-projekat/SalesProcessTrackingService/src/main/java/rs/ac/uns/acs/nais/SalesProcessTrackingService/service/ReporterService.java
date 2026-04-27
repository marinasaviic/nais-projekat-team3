package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Reporter;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.ReporterRepository;

import java.util.List;

@Service
public class ReporterService {

    @Autowired
    private ReporterRepository reporterRepository;

    public Reporter save(Reporter reporter) {
        return reporterRepository.save(reporter);
    }

    public List<Reporter> findAll() {
        return reporterRepository.findAll();
    }

    public Reporter update(Long id, Reporter updatedReporter) {
        Reporter existing = reporterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        existing.setFullName(updatedReporter.getFullName());
        existing.setType(updatedReporter.getType());
        existing.setAge(updatedReporter.getAge());
        existing.setGender(updatedReporter.getGender());

        return reporterRepository.save(existing);
    }

    public void delete(Long id) {
        reporterRepository.deleteById(id);
    }

    public Reporter connectReporterToReport(Long reporterId, Long reportId) {
        return reporterRepository.connectReporterToReport(reporterId, reportId);
    }
}