package collab.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import collab.model.Region;

public interface RegionRepository extends Neo4jRepository<Region, String> {
}
