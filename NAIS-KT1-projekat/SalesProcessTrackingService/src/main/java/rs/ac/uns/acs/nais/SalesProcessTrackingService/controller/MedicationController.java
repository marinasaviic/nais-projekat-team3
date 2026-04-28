package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Medication;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.MedicationService;

import java.util.List;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @PostMapping
    public Medication create(@RequestBody Medication medication) {
        return medicationService.save(medication);
    }

    @GetMapping
    public List<Medication> getAll() {
        return medicationService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        medicationService.delete(id);
    }
    @PostMapping("/{medicationId}/side-effects/{sideEffectId}")
    public Medication connectToSideEffect(@PathVariable Long medicationId,
                                          @PathVariable Long sideEffectId,
                                          @RequestParam Integer frequency,
                                          @RequestParam String riskLevel) {
        return medicationService.connectToSideEffect(medicationId, sideEffectId, frequency, riskLevel);
    }
    @PutMapping("/{id}")
    public Medication update(@PathVariable Long id, @RequestBody Medication medication) {
        return medicationService.update(id, medication);
    }
}