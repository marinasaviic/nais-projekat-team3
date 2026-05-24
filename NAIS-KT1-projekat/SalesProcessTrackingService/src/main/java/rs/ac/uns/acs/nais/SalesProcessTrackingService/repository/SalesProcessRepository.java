package rs.ac.uns.acs.nais.SalesProcessTrackingService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesProcess;

import java.util.List;

public interface SalesProcessRepository extends Neo4jRepository<SalesProcess, String> {

    @Query("""
        MATCH (c:Customer {id: $customerId}), (sp:SalesProcess {id: $processId})
        CREATE (c)-[:HAS_PROCESS]->(sp)
        RETURN sp
    """)
    SalesProcess connectCustomerToProcess(String customerId, String processId);

    @Query("""
        MATCH (sr:SalesRepresentative {id: $representativeId}), (sp:SalesProcess {id: $processId})
        CREATE (sr)-[:MANAGES]->(sp)
        RETURN sp
    """)
    SalesProcess connectRepresentativeToProcess(String representativeId, String processId);

    @Query("""
        MATCH (sp:SalesProcess {id: $processId}), (s:Stage {id: $stageId})
        CREATE (sp)-[:CURRENT_STAGE]->(s)
        RETURN sp
    """)
    SalesProcess setCurrentStage(String processId, String stageId);

    @Query("""
        MATCH (:Customer {id: $customerId})-[r:HAS_PROCESS]->(:SalesProcess {id: $processId})
        DELETE r
    """)
    void removeCustomerProcessRelation(String customerId, String processId);

    @Query("""
        MATCH (:SalesRepresentative {id: $representativeId})-[r:MANAGES]->(:SalesProcess {id: $processId})
        DELETE r
    """)
    void removeRepresentativeProcessRelation(String representativeId, String processId);

    @Query("""
        MATCH (sp:SalesProcess {id: $processId})-[r:CURRENT_STAGE]->(:Stage)
        DELETE r
    """)
    void removeCurrentStageRelation(String processId);

    @Query("""
        MATCH (sp:SalesProcess)-[:CURRENT_STAGE]->(s:Stage)
        WHERE sp.status IS NOT NULL
        WITH s.name AS stage, COUNT(sp) AS total
        RETURN stage + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countProcessesByStage();

    @Query("""
        MATCH (sr:SalesRepresentative)-[:MANAGES]->(sp:SalesProcess)
        WHERE sp.status IS NOT NULL
        WITH sr.name AS representative, COUNT(sp) AS total
        RETURN representative + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countProcessesByRepresentative();

    @Query("""
        MATCH (c:Customer)-[:HAS_PROCESS]->(sp:SalesProcess)
        WHERE sp.status IS NOT NULL
        WITH c.name AS customer, COUNT(sp) AS total
        WHERE total > 1
        RETURN customer + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> findCustomersWithMultipleProcesses();

    @Query("""
        MATCH (s1:Stage {name: $stageName})-[:ALLOWED_TO]->(s2:Stage)
        RETURN s1.name + ' -> ' + s2.name
    """)
    List<String> findAllowedTransitions(String stageName);

    @Query("""
        MATCH (sp:SalesProcess)-[:CURRENT_STAGE]->(s:Stage)
        WHERE sp.status IS NOT NULL AND s.name IS NOT NULL
        WITH sp.status AS status, s.name AS stage, COUNT(*) AS total
        RETURN status + ' | ' + stage + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> countProcessesByStatusAndStage();

    @Query("""
        MATCH (sr:SalesRepresentative)-[:MANAGES]->(sp:SalesProcess)<-[:HAS_PROCESS]-(c:Customer),
            (sp)-[:CURRENT_STAGE]->(stage:Stage)
        WHERE sp.status = 'ACTIVE'
        RETURN sr.name + ' vodi proces ' + sp.title
            + ' za kupca ' + c.name
            + ' iz grada ' + c.city
            + ', trenutna faza je ' + stage.name
        ORDER BY sr.name, c.city
        LIMIT 30
    """)
    List<String> findActiveProcessesWithCustomerRepresentativeAndStage();

    @Query("""
        MATCH (sr:SalesRepresentative)-[:MANAGES]->(sp:SalesProcess)<-[:HAS_PROCESS]-(c:Customer),
            (sp)-[:CURRENT_STAGE]->(stage:Stage)
        WITH sr.name AS representative, c.city AS city, stage.name AS stageName, COUNT(sp) AS total
        RETURN representative + ' | grad: ' + city
            + ' | faza: ' + stageName
            + ' | broj procesa: ' + toString(total)
        ORDER BY total DESC
        LIMIT 30
    """)
    List<String> countProcessesByRepresentativeCityAndStage();

    @Query("""
        MATCH path = (start:Stage)-[:ALLOWED_TO*1..3]->(end:Stage)
        WHERE start.name = 'Lead Qualification'
        RETURN start.name + ' -> ' + end.name
            + ' | broj koraka: ' + toString(length(path))
        ORDER BY length(path)
    """)
    List<String> findStagePathsFromQualification();

    @Query("""
        MATCH (c:Customer)-[:HAS_PROCESS]->(sp:SalesProcess)-[:CURRENT_STAGE]->(stage:Stage)
        WHERE stage.name = 'Negotiation' OR stage.name = 'Offer Sent'
        RETURN c.name + ' | proces: ' + sp.title
            + ' | status: ' + sp.status
            + ' | faza: ' + stage.name
        ORDER BY c.name
        LIMIT 30
    """)
    List<String> findProcessesInImportantSalesStages();
}