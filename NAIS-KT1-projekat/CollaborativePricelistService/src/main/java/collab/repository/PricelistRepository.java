package collab.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import collab.model.Pricelist;

public interface PricelistRepository extends Neo4jRepository<Pricelist, String> {
}
