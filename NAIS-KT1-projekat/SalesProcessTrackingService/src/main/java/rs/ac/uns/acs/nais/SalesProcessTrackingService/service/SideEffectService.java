package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SideEffect;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.SideEffectRepository;

import java.util.List;

@Service
public class SideEffectService {

    @Autowired
    private SideEffectRepository sideEffectRepository;

    public SideEffect save(SideEffect sideEffect) {
        return sideEffectRepository.save(sideEffect);
    }

    public List<SideEffect> findAll() {
        return sideEffectRepository.findAll();
    }

    public SideEffect update(Long id, SideEffect updatedSideEffect) {
        SideEffect existing = sideEffectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Side effect not found"));

        existing.setName(updatedSideEffect.getName());
        existing.setDescription(updatedSideEffect.getDescription());
        existing.setSeverity(updatedSideEffect.getSeverity());

        return sideEffectRepository.save(existing);
    }

    public void delete(Long id) {
        sideEffectRepository.deleteById(id);
    }
}