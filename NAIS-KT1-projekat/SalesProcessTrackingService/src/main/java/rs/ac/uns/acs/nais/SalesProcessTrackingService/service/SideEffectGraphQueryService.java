package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SideEffectGraphQueryService {

    private final Neo4jClient neo4jClient;

    public SideEffectGraphQueryService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<Map<String, Object>> medicationSideEffectsFrequency() {
        return new ArrayList<>(neo4jClient.query("""
                MATCH (m:Medication)-[:ASSOCIATED_WITH]->(s:SideEffect)
                WHERE m.name IS NOT NULL AND s.name IS NOT NULL
                WITH m.name AS medication, s.name AS sideEffect, count(*) AS frequency
                RETURN medication, sideEffect, frequency
                ORDER BY frequency DESC
                """).fetch().all());
    }

    public List<Map<String, Object>> medicationsWithMostReports() {
        return new ArrayList<>(neo4jClient.query("""
                MATCH (r:Report)-[:FOR_MEDICATION]->(m:Medication)
                WHERE m.name IS NOT NULL
                WITH m.name AS medication, count(r) AS reports
                RETURN medication, reports
                ORDER BY reports DESC
                """).fetch().all());
    }

    public List<Map<String, Object>> reportsBySideEffect() {
        return new ArrayList<>(neo4jClient.query("""
                MATCH (s:SideEffect)<-[:HAS_SIDE_EFFECT]-(r:Report)
                WHERE s.name IS NOT NULL
                WITH s.name AS sideEffect, s.severity AS severity, count(r) AS numberOfReports
                RETURN sideEffect, severity, numberOfReports
                ORDER BY numberOfReports DESC
                """).fetch().all());
    }

    public List<Map<String, Object>> similarReports() {
        return new ArrayList<>(neo4jClient.query("""
                MATCH (r1:Report)-[:FOR_MEDICATION]->(m:Medication),
                      (r1)-[:HAS_SIDE_EFFECT]->(s:SideEffect),
                      (r2:Report)-[:FOR_MEDICATION]->(m),
                      (r2)-[:HAS_SIDE_EFFECT]->(s)
                WHERE id(r1) < id(r2)
                WITH m.name AS medication, s.name AS sideEffect, count(r2) AS similarReports
                RETURN medication, sideEffect, similarReports
                ORDER BY similarReports DESC
                """).fetch().all());
    }

    public List<Map<String, Object>> analystWorkload() {
        return new ArrayList<>(neo4jClient.query("""
                MATCH (a:Analyst)-[:ANALYZED]->(r:Report)
                WHERE a.fullName IS NOT NULL
                WITH a.fullName AS analyst, count(r) AS reportsAnalyzed
                RETURN analyst, reportsAnalyzed
                ORDER BY reportsAnalyzed DESC
                """).fetch().all());
    }
}