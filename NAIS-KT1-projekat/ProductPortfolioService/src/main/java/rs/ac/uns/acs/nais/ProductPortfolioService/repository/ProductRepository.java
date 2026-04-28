package rs.ac.uns.acs.nais.ProductPortfolioService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends Neo4jRepository<Product, String> {

    Optional<Product> findByName(String name);

    Optional<Product> findByCode(String code);

    @Query("""
        MATCH (p:Product {id: $productId}), (c:Category {id: $categoryId})
        WHERE NOT EXISTS {
            MATCH (p)-[:BELONGS_TO]->(c)
        }
        CREATE (p)-[:BELONGS_TO]->(c)
        RETURN p
    """)
    Product connectProductToCategory(String productId, String categoryId);

    @Query("""
        MATCH (p:Product {id: $productId})-[r:BELONGS_TO]->(:Category)
        DELETE r
        RETURN p
    """)
    Product removeProductCategoryRelation(String productId);

    @Query("""
        MATCH (p:Product {id: $productId}), (v:ProductVariant {id: $variantId})
        WHERE NOT EXISTS {
            MATCH (p)-[:HAS_VARIANT]->(v)
        }
        CREATE (p)-[:HAS_VARIANT]->(v)
        RETURN p
    """)
    Product connectProductToVariant(String productId, String variantId);

    @Query("""
        MATCH (p:Product {id: $productId})-[r:HAS_VARIANT]->(:ProductVariant {id: $variantId})
        DELETE r
        RETURN p
    """)
    Product removeProductVariantRelation(String productId, String variantId);

    @Query("""
        MATCH (p:Product {id: $productId})-[r:HAS_STATUS]->(:LifecycleStatus)
        DELETE r
        RETURN p
    """)
    Product removeProductStatusRelation(String productId);

    @Query("""
        MATCH (p:Product {id: $productId}), (s:LifecycleStatus {id: $statusId})
        WHERE NOT EXISTS {
            MATCH (p)-[:HAS_STATUS]->(s)
        }
        CREATE (p)-[:HAS_STATUS]->(s)
        RETURN p
    """)
    Product connectProductToStatus(String productId, String statusId);

    @Query("""
        MATCH (p:Product)-[:BELONGS_TO]->(c:Category)
        WHERE c.name IS NOT NULL
        WITH c.name AS category, COUNT(p) AS total
        RETURN category + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countProductsByCategory();

    @Query("""
        MATCH (p:Product)-[:HAS_STATUS]->(s:LifecycleStatus)
        WHERE s.name IS NOT NULL
        WITH s.name AS status, COUNT(p) AS total
        RETURN status + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countProductsByStatus();

    @Query("""
        MATCH (p:Product)-[:HAS_VARIANT]->(v:ProductVariant)
        WHERE p.name IS NOT NULL
        WITH p.name AS product, COUNT(v) AS total
        WHERE total > 1
        RETURN product + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> findProductsWithMultipleVariants();

    @Query("""
        MATCH (p:Product)-[:BELONGS_TO]->(c:Category)
        MATCH (p)-[:HAS_STATUS]->(s:LifecycleStatus)
        MATCH (p)-[:HAS_VARIANT]->(v:ProductVariant)-[:AVAILABLE_IN]->(m:Market)
        WHERE s.name = 'ACTIVE'
        WITH c.name AS category, m.countryName AS market, COUNT(DISTINCT p) AS total
        RETURN category + ' | ' + market + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countActiveProductsByCategoryAndMarket();
}