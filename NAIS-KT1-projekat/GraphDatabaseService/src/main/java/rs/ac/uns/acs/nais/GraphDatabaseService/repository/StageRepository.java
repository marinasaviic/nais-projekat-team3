package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;

import java.util.Optional;

public interface StageRepository extends Neo4jRepository<Stage, String> {
    Optional<Stage> findByName(String name);
}