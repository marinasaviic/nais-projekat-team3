package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Stage;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface StageRepository extends Neo4jRepository<Stage, String> {
    Optional<Stage> findByName(String name);

    @Query("""
        MATCH (from:Stage {id: $fromStageId}), (to:Stage {id: $toStageId})
        CREATE (from)-[:ALLOWED_TO]->(to)
        RETURN from
    """)
    Stage connectStageToStage(String fromStageId, String toStageId);

    @Query("""
        MATCH (from:Stage {id: $fromStageId})-[r:ALLOWED_TO]->(to:Stage {id: $toStageId})
        DELETE r
        RETURN from
    """)
    Stage removeStageToStageRelation(String fromStageId, String toStageId);
}