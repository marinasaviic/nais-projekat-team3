package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SideEffect;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.SideEffectService;

import java.util.List;

@RestController
@RequestMapping("/side-effects")
public class SideEffectController {

    @Autowired
    private SideEffectService sideEffectService;

    @PostMapping
    public SideEffect create(@RequestBody SideEffect sideEffect) {
        return sideEffectService.save(sideEffect);
    }

    @GetMapping
    public List<SideEffect> getAll() {
        return sideEffectService.findAll();
    }

    @PutMapping("/{id}")
    public SideEffect update(@PathVariable Long id, @RequestBody SideEffect sideEffect) {
        return sideEffectService.update(id, sideEffect);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sideEffectService.delete(id);
    }
}