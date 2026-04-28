package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Reporter;

public interface ReporterRepository extends Neo4jRepository<Reporter, Long> {

    @Query("""
            MATCH (rep:Reporter), (r:Report)
            WHERE id(rep) = $reporterId AND id(r) = $reportId
            MERGE (rep)-[:CREATED]->(r)
            RETURN rep
            """)
    Reporter connectReporterToReport(@Param("reporterId") Long reporterId,
                                     @Param("reportId") Long reportId);
}