package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Team;

import java.time.ZonedDateTime;
import java.util.List;

public interface CollaborationRepository extends Neo4jRepository<Team, String> {

    @Query("""
        MATCH (u:TeamUser {id: $userId}), (t:Team {id: $teamId})
        MERGE (u)-[r:MEMBER_OF]->(t)
        SET r.role = $role, r.assignedAt = $assignedAt
        RETURN t
    """)
    Team addUserToTeam(String userId, String teamId, String role, ZonedDateTime assignedAt);

    @Query("""
        MATCH (:TeamUser {id: $userId})-[r:MEMBER_OF]->(:Team {id: $teamId})
        SET r.role = $newRole
    """)
    void updateUserTeamRole(String userId, String teamId, String newRole);

    @Query("""
        MATCH (:TeamUser {id: $userId})-[r:MEMBER_OF]->(:Team {id: $teamId})
        DELETE r
    """)
    void removeUserFromTeam(String userId, String teamId);

    @Query("""
        MATCH (t:Team {id: $teamId}), (p:Pricelist {id: $pricelistId})
        MERGE (t)-[r:WORKS_ON]->(p)
        SET r.ownershipType = $ownershipType, r.assignedAt = $assignedAt
        RETURN t
    """)
    Team assignTeamToPricelist(String teamId, String pricelistId, String ownershipType, ZonedDateTime assignedAt);

    @Query("""
        MATCH (:Team {id: $teamId})-[r:WORKS_ON]->(:Pricelist {id: $pricelistId})
        SET r.ownershipType = $newOwnershipType
    """)
    void updateTeamPricelistOwnership(String teamId, String pricelistId, String newOwnershipType);

    @Query("""
        MATCH (:Team {id: $teamId})-[r:WORKS_ON]->(:Pricelist {id: $pricelistId})
        DELETE r
    """)
    void unassignTeamFromPricelist(String teamId, String pricelistId);

    @Query("""
        MATCH (p:Pricelist {id: $pricelistId}), (r:Region {id: $regionId})
        MERGE (p)-[fr:FOR_REGION]->(r)
        SET fr.coverageLevel = $coverageLevel
        RETURN p
    """)
    rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist connectPricelistToRegion(
            String pricelistId,
            String regionId,
            String coverageLevel
    );

    @Query("""
        MATCH (:Pricelist {id: $pricelistId})-[fr:FOR_REGION]->(:Region {id: $regionId})
        SET fr.coverageLevel = $newCoverageLevel
    """)
    void updatePricelistRegionCoverage(String pricelistId, String regionId, String newCoverageLevel);

    @Query("""
        MATCH (:Pricelist {id: $pricelistId})-[fr:FOR_REGION]->(:Region {id: $regionId})
        DELETE fr
    """)
    void disconnectPricelistFromRegion(String pricelistId, String regionId);

    @Query("""
        MATCH (u:TeamUser {id: $userId}), (p:Pricelist {id: $pricelistId})
        CREATE (u)-[:PERFORMED {
            actionType: $actionType,
            timestamp: $timestamp,
            durationMinutes: $durationMinutes
        }]->(p)
    """)
    void logUserActionOnPricelist(String userId, String pricelistId, String actionType, ZonedDateTime timestamp, Integer durationMinutes);

    @Query("""
        MATCH (:TeamUser {id: $userId})-[a:PERFORMED]->(:Pricelist {id: $pricelistId})
        WHERE a.timestamp = $timestamp
        DELETE a
    """)
    void deleteUserActionOnPricelist(String userId, String pricelistId, ZonedDateTime timestamp);

    @Query("""
        MATCH (t:Team)-[:WORKS_ON]->(p:Pricelist)
        WHERE p.status = 'ACTIVE'
        WITH t, COUNT(p) AS total
        RETURN t.name + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> teamsWorkingOnMostPricelists();

    @Query("""
        MATCH (p:Pricelist)-[:FOR_REGION]->(r:Region)
        WHERE p.status = 'ACTIVE'
        WITH r, COUNT(p) AS total
        RETURN r.name + ' | ' + toString(total)
        ORDER BY total DESC
    """)
    List<String> regionsWithMostActivePricelists();

    @Query("""
        MATCH (u:TeamUser)-[:MEMBER_OF]->(t:Team)
        WHERE u.name IS NOT NULL
        WITH u, COUNT(t) AS totalTeams
        WHERE totalTeams > 1
        RETURN u.name + ' | ' + toString(totalTeams)
        ORDER BY totalTeams DESC
    """)
    List<String> usersAssignedToMultipleTeams();

    @Query("""
        MATCH (u:TeamUser)-[:MEMBER_OF]->(t:Team)
        WHERE t.name IS NOT NULL
        WITH t, COUNT(u) AS totalMembers
        RETURN t.name + ' | ' + toString(totalMembers)
        ORDER BY totalMembers DESC
    """)
    List<String> teamsWithMostMembers();

    @Query("""
        MATCH (p:Pricelist)
        WHERE NOT ( (:Team)-[:WORKS_ON]->(p) )
        WITH p, p.status AS status
        RETURN p.name + ' | ' + coalesce(status, 'UNKNOWN')
        ORDER BY p.name
    """)
    List<String> pricelistsWithoutAssignedTeams();

    @Query("""
        MATCH (u:TeamUser)-[a:PERFORMED]->(p:Pricelist)
        WHERE a.actionType IN ['CREATE', 'UPDATE']
        WITH u, COUNT(a) AS totalChanges
        RETURN u.name + ' | ' + toString(totalChanges)
        ORDER BY totalChanges DESC
    """)
    List<String> usersWithMostChanges();

    @Query("""
        MATCH (u:TeamUser)-[:MEMBER_OF]->(t:Team)
        MATCH (u)-[a:PERFORMED]->(:Pricelist)
        WHERE a.durationMinutes IS NOT NULL
        WITH t, AVG(toFloat(a.durationMinutes)) AS avgMinutes, COUNT(a) AS totalActions
        WHERE totalActions > 0
        RETURN t.name + ' | ' + toString(round(avgMinutes)) + ' min'
        ORDER BY avgMinutes DESC
    """)
    List<String> averageWorkTimeByTeam();

    @Query("""
        MATCH (u:TeamUser)-[a:PERFORMED]->(p:Pricelist)
        WHERE a.timestamp >= datetime() - duration({days: $days})
        WITH u, p, a
        RETURN u.name + ' | ' + p.name + ' | ' + a.actionType + ' | ' + toString(a.timestamp)
        ORDER BY a.timestamp DESC
    """)
    List<String> activitiesInLastNDays(Integer days);

    @Query("""
        MATCH (u:TeamUser)-[a:PERFORMED]->(p:Pricelist)-[:FOR_REGION]->(r:Region)
        WHERE a.actionType IS NOT NULL
        WITH r, u, COUNT(a) AS totalActions
        ORDER BY r.name, totalActions DESC
        WITH r, collect(u.name + ' | ' + toString(totalActions))[0] AS topUser
        RETURN r.name + ' | ' + topUser
    """)
    List<String> mostActiveUserPerRegion();

    // ============================================================================
    // FZ 2.4.1: Praćenje aktivnosti timova - PERFORMANCE EVALUATION QUERIES
    // ============================================================================

    @Query("""
        MATCH (t:Team)-[:MEMBER_OF|WORKS_ON]-(n)
        WITH t
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        WITH t, COUNT(DISTINCT u) AS memberCount
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)-[:WORKS_ON]->(p:Pricelist)
        WITH t, memberCount, COUNT(DISTINCT p) AS pricelistCount
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        OPTIONAL MATCH (u)-[a:PERFORMED]->(:Pricelist)
        WHERE a.durationMinutes IS NOT NULL
        WITH t, memberCount, pricelistCount, COLLECT(a.durationMinutes) AS durations
        RETURN t.name + ' | Members: ' + toString(memberCount) + ' | Pricelists: ' + toString(pricelistCount) + 
               ' | Total Work: ' + toString(COALESCE(reduce(sum = 0, d in durations | sum + d), 0)) + ' min' AS performanceMetric
        ORDER BY pricelistCount DESC
    """)
    List<String> teamPerformanceDashboard();

    @Query("""
        MATCH (t:Team {id: $teamId})-[:MEMBER_OF|WORKS_ON]-(n)
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        WITH t, u
        OPTIONAL MATCH (u)-[a:PERFORMED]->(p:Pricelist)<-[:WORKS_ON]-(t)
        WHERE a.actionType IS NOT NULL
        WITH t, COUNT(DISTINCT u) AS memberCount, COUNT(a) AS totalActions,
             COLLECT(DISTINCT a.actionType) AS actionTypes,
             AVG(COALESCE(toFloat(a.durationMinutes), 0)) AS avgWorkTime,
             SUM(COALESCE(a.durationMinutes, 0)) AS totalWorkTime
        RETURN {
            teamId: t.id,
            teamName: t.name,
            memberCount: memberCount,
            totalActions: totalActions,
            actionTypes: actionTypes,
            averageWorkTimePerAction: round(avgWorkTime),
            totalWorkTime: totalWorkTime
        }
    """)
    String getTeamPerformanceMetrics(String teamId);

    @Query("""
        MATCH (t:Team {id: $teamId})-[:MEMBER_OF|WORKS_ON]-(n)
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        OPTIONAL MATCH (u)-[a:PERFORMED]->(:Pricelist)<-[:WORKS_ON]-(t)
        WHERE a.timestamp >= datetime() - duration({days: $days})
        WITH t, u, a
        RETURN u.name + ' | Actions: ' + toString(COUNT(a)) + 
               ' | Time: ' + toString(SUM(COALESCE(a.durationMinutes, 0))) + ' min | Types: ' + 
               REDUCE(s = '', type in COLLECT(DISTINCT a.actionType) | s + type + ',') AS userActivity
        ORDER BY COUNT(a) DESC
    """)
    List<String> getTeamActivitySummaryByPeriod(String teamId, Integer days);

    @Query("""
        MATCH (u:TeamUser)-[:MEMBER_OF]->(t:Team)
        OPTIONAL MATCH (u)-[a:PERFORMED]->(:Pricelist)<-[:WORKS_ON]-(t)
        WHERE a.actionType IS NOT NULL
        WITH u, t, a.actionType AS actionType, COUNT(a) AS count
        RETURN u.name + ' (' + t.name + ') | ' + actionType + ': ' + toString(count)
        ORDER BY u.name, count DESC
    """)
    List<String> getUserActionCountByTeamAndType();

    @Query("""
        MATCH (t:Team)-[:WORKS_ON]->(p:Pricelist)
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        OPTIONAL MATCH (u)-[a:PERFORMED]->(p)
        WHERE a.timestamp >= datetime() - duration({days: $days})
        WITH t, p, COUNT(a) AS actionCount, SUM(COALESCE(a.durationMinutes, 0)) AS workTime
        WHERE actionCount > 0
        RETURN t.name + ' | ' + p.name + ' | Actions: ' + toString(actionCount) + ' | Work: ' + toString(workTime) + ' min'
        ORDER BY actionCount DESC
    """)
    List<String> getTeamPricelistActivityInPeriod(Integer days);

    @Query("""
        MATCH (t:Team)-[:WORKS_ON]->(p:Pricelist)
        OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
        OPTIONAL MATCH (u)-[a:PERFORMED]->(p)
        WHERE a.actionType IN ['CREATE', 'UPDATE', 'DELETE']
        WITH t, COUNT(DISTINCT u) AS members, COUNT(a) AS changes, SUM(COALESCE(a.durationMinutes, 0)) AS totalTime
        WHERE changes > 0
        RETURN t.name + ' | Members: ' + toString(members) + ' | Changes: ' + toString(changes) + ' | Time: ' + toString(totalTime) + ' min'
        ORDER BY changes DESC
    """)
    List<String> getTeamProductivityMetrics();
}
