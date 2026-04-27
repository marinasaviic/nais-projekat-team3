package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;


import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SideEffect;

public interface SideEffectRepository extends Neo4jRepository<SideEffect, Long> {
}