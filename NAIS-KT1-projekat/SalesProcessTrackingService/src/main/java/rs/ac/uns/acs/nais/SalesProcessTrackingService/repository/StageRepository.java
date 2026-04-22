package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Stage;

import java.util.Optional;

public interface StageRepository extends Neo4jRepository<Stage, String> {
    Optional<Stage> findByName(String name);
}