package collab.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import collab.model.Pricelist;

public interface PricelistRepository extends Neo4jRepository<Pricelist, String> {

	@Query("""
		MATCH (p:Pricelist)-[:FOR_REGION]->(r:Region {id: $regionId})
		WHERE p.status = 'ACTIVE'
		RETURN p
	""")
	java.util.List<Pricelist> findActivePricelistsForRegion(String regionId);

}
