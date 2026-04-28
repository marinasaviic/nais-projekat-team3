package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;

import java.time.ZonedDateTime;
import java.util.List;

public interface ActivityLogRepository extends Neo4jRepository<ActivityLog, String> {
    
    // Pronalazi sve aktivnosti korisnika nakon određenog vremena
    @Query("MATCH (a:ActivityLog) WHERE a.userId = $userId AND a.timestamp > $timestamp RETURN a ORDER BY a.timestamp DESC")
    List<ActivityLog> findByUserIdAndTimestampAfterOrderByTimestampDesc(String userId, ZonedDateTime timestamp);
    
    // Pronalazi sve aktivnosti na cenovniku
    List<ActivityLog> findByPricelistIdOrderByTimestampDesc(String pricelistId);
    
    // Pronalazi sve aktivnosti tima
    List<ActivityLog> findByTeamIdOrderByTimestampDesc(String teamId);
    
    // Pronalazi sve aktivnosti određenog tipa
    List<ActivityLog> findByActionTypeOrderByTimestampDesc(String actionType);
    
    // Pronalazi sve aktivnosti u vremenskom rasponu
    List<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(ZonedDateTime startDate, ZonedDateTime endDate);
    
    // Pronalazi sve aktivnosti korisnika
    List<ActivityLog> findByUserId(String userId);
}
