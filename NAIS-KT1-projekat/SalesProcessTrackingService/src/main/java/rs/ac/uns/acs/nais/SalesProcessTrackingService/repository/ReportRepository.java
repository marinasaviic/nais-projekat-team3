package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Report;

public interface ReportRepository extends Neo4jRepository<Report, Long> {

    @Query("""
            MATCH (r:Report), (m:Medication)
            WHERE id(r) = $reportId AND id(m) = $medicationId
            MERGE (r)-[:FOR_MEDICATION]->(m)
            RETURN r
            """)
    Report connectReportToMedication(@Param("reportId") Long reportId,
                                     @Param("medicationId") Long medicationId);

    @Query("""
            MATCH (r:Report), (s:SideEffect)
            WHERE id(r) = $reportId AND id(s) = $sideEffectId
            MERGE (r)-[:HAS_SIDE_EFFECT]->(s)
            RETURN r
            """)
    Report connectReportToSideEffect(@Param("reportId") Long reportId,
                                     @Param("sideEffectId") Long sideEffectId);
}