package collab.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import collab.model.TeamUser;

public interface TeamUserRepository extends Neo4jRepository<TeamUser, String> {
}
