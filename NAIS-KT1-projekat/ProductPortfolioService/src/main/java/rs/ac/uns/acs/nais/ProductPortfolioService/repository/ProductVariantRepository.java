package rs.ac.uns.acs.nais.ProductPortfolioService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.ProductVariant;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends Neo4jRepository<ProductVariant, String> {

    Optional<ProductVariant> findByName(String name);

    @Query("""
        MATCH (v:ProductVariant {id: $variantId}), (m:Market {id: $marketId})
        WHERE NOT EXISTS {
            MATCH (v)-[:AVAILABLE_IN]->(m)
        }
        CREATE (v)-[:AVAILABLE_IN]->(m)
        RETURN v
    """)
    ProductVariant connectVariantToMarket(String variantId, String marketId);

    @Query("""
        MATCH (v:ProductVariant {id: $variantId})-[r:AVAILABLE_IN]->(:Market {id: $marketId})
        DELETE r
        RETURN v
    """)
    ProductVariant removeVariantMarketRelation(String variantId, String marketId);

    @Query("""
        MATCH (v:ProductVariant)-[:AVAILABLE_IN]->(m:Market)
        WHERE m.countryName IS NOT NULL
        WITH m.countryName AS market, COUNT(v) AS total
        RETURN market + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countVariantsByMarket();
}