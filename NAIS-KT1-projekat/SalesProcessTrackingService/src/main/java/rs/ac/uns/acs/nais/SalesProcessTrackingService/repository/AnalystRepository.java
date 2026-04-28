package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Analyst;

public interface AnalystRepository extends Neo4jRepository<Analyst, Long> {

    @Query("""
            MATCH (a:Analyst), (r:Report)
            WHERE id(a) = $analystId AND id(r) = $reportId
            MERGE (a)-[rel:ANALYZED]->(r)
            SET rel.causalityAssessment = $causalityAssessment,
                rel.note = $note
            RETURN a
            """)
    Analyst connectAnalystToReport(@Param("analystId") Long analystId,
                                   @Param("reportId") Long reportId,
                                   @Param("causalityAssessment") String causalityAssessment,
                                   @Param("note") String note);
}