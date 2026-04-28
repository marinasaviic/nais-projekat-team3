package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Region;

public interface RegionRepository extends Neo4jRepository<Region, String> {
}
