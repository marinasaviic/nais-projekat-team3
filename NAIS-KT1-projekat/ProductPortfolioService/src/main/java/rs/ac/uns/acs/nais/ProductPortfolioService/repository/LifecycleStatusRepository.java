package rs.ac.uns.acs.nais.ProductPortfolioService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.LifecycleStatus;

import java.util.Optional;

public interface LifecycleStatusRepository extends Neo4jRepository<LifecycleStatus, String> {

    Optional<LifecycleStatus> findByName(String name);
}