package rs.ac.uns.acs.nais.ProductPortfolioService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Market;

import java.util.Optional;

public interface MarketRepository extends Neo4jRepository<Market, String> {

    Optional<Market> findByCountryName(String countryName);
}