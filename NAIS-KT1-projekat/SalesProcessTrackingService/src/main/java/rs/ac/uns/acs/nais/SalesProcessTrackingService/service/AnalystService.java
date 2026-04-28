package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Analyst;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.AnalystRepository;

import java.util.List;

@Service
public class AnalystService {

    @Autowired
    private AnalystRepository analystRepository;

    public Analyst save(Analyst analyst) {
        return analystRepository.save(analyst);
    }

    public List<Analyst> findAll() {
        return analystRepository.findAll();
    }

    public Analyst update(Long id, Analyst updatedAnalyst) {
        Analyst existing = analystRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyst not found"));

        existing.setFullName(updatedAnalyst.getFullName());
        existing.setEmail(updatedAnalyst.getEmail());
        existing.setDepartment(updatedAnalyst.getDepartment());

        return analystRepository.save(existing);
    }

    public void delete(Long id) {
        analystRepository.deleteById(id);
    }

    public Analyst connectAnalystToReport(Long analystId, Long reportId, String causalityAssessment, String note) {
        return analystRepository.connectAnalystToReport(analystId, reportId, causalityAssessment, note);
    }
}