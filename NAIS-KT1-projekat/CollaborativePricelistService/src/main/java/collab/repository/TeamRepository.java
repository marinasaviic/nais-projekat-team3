package collab.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import collab.model.Team;

public interface TeamRepository extends Neo4jRepository<Team, String> {
}
