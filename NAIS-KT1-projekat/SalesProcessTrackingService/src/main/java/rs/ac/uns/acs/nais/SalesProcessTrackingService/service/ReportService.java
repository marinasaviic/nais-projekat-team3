package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Report;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.ReportRepository;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Report save(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report update(Long id, Report updatedReport) {
        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        existing.setReportDate(updatedReport.getReportDate());
        existing.setSymptomDate(updatedReport.getSymptomDate());
        existing.setStatus(updatedReport.getStatus());
        existing.setSeverity(updatedReport.getSeverity());
        existing.setDescription(updatedReport.getDescription());
        existing.setAnalysisConclusion(updatedReport.getAnalysisConclusion());

        return reportRepository.save(existing);
    }

    public void delete(Long id) {
        reportRepository.deleteById(id);
    }

    public Report connectReportToMedication(Long reportId, Long medicationId) {
        return reportRepository.connectReportToMedication(reportId, medicationId);
    }

    public Report connectReportToSideEffect(Long reportId, Long sideEffectId) {
        return reportRepository.connectReportToSideEffect(reportId, sideEffectId);
    }
}