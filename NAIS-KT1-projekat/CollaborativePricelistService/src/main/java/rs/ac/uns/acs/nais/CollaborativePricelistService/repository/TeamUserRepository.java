package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.TeamUser;

public interface TeamUserRepository extends Neo4jRepository<TeamUser, String> {
}
