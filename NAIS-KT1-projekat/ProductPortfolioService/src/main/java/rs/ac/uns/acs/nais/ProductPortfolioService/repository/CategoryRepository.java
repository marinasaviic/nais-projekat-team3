package rs.ac.uns.acs.nais.ProductPortfolioService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.ProductPortfolioService.model.Category;

import java.util.Optional;

public interface CategoryRepository extends Neo4jRepository<Category, String> {

    Optional<Category> findByName(String name);

    @Query("""
        MATCH (child:Category {id: $childCategoryId}), (parent:Category {id: $parentCategoryId})
        WHERE NOT EXISTS {
            MATCH (child)-[:SUBCATEGORY_OF]->(parent)
        }
        CREATE (child)-[:SUBCATEGORY_OF]->(parent)
        RETURN child
    """)
    Category connectSubcategoryToParent(String childCategoryId, String parentCategoryId);

    @Query("""
        MATCH (child:Category {id: $childCategoryId})-[r:SUBCATEGORY_OF]->(:Category {id: $parentCategoryId})
        DELETE r
        RETURN child
    """)
    Category removeSubcategoryRelation(String childCategoryId, String parentCategoryId);
}