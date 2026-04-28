package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Medication;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.MedicationRepository;

import java.util.List;

@Service
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    public Medication save(Medication medication) {
        return medicationRepository.save(medication);
    }

    public List<Medication> findAll() {
        return medicationRepository.findAll();
    }

    public void delete(Long id) {
        medicationRepository.deleteById(id);
    }
    public Medication update(Long id, Medication updatedMedication) {
        Medication existing = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        existing.setName(updatedMedication.getName());
        existing.setDosage(updatedMedication.getDosage());
        existing.setUsageMethod(updatedMedication.getUsageMethod());

        return medicationRepository.save(existing);
    }
    public Medication connectToSideEffect(Long medicationId, Long sideEffectId, Integer frequency, String riskLevel) {
        return medicationRepository.connectToSideEffect(medicationId, sideEffectId, frequency, riskLevel);
    }
}
