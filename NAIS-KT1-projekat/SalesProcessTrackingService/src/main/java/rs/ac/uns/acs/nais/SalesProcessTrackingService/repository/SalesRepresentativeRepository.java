package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesRepresentative;

import java.util.Optional;

public interface SalesRepresentativeRepository extends Neo4jRepository<SalesRepresentative, String> {
    Optional<SalesRepresentative> findByName(String name);
}