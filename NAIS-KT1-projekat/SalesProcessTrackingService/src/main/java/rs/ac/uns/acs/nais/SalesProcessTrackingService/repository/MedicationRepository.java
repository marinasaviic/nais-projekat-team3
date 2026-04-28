package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Medication;

public interface MedicationRepository extends Neo4jRepository<Medication, Long> {

    @Query("""
            MATCH (m:Medication), (s:SideEffect)
            WHERE id(m) = $medicationId AND id(s) = $sideEffectId
            MERGE (m)-[r:ASSOCIATED_WITH]->(s)
            SET r.frequency = $frequency,
                r.riskLevel = $riskLevel
            RETURN m
            """)
    Medication connectToSideEffect(@Param("medicationId") Long medicationId,
                                   @Param("sideEffectId") Long sideEffectId,
                                   @Param("frequency") Integer frequency,
                                   @Param("riskLevel") String riskLevel);
}