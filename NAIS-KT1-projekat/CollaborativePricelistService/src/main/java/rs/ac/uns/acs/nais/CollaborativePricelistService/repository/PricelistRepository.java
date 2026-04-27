package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist;

public interface PricelistRepository extends Neo4jRepository<Pricelist, String> {
}
