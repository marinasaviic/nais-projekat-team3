package rs.ac.uns.acs.nais.CollaborativePricelistService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends Neo4jRepository<ActivityLog, String> {
    
    // Pronalazi sve aktivnosti korisnika nakon određenog vremena
    List<ActivityLog> findByUserIdAndTimestampAfterOrderByTimestampDesc(String userId, LocalDateTime timestamp);
    
    // Pronalazi sve aktivnosti na cenovniku
    List<ActivityLog> findByPricelistIdOrderByTimestampDesc(String pricelistId);
    
    // Pronalazi sve aktivnosti tima
    List<ActivityLog> findByTeamIdOrderByTimestampDesc(String teamId);
    
    // Pronalazi sve aktivnosti određenog tipa
    List<ActivityLog> findByActionTypeOrderByTimestampDesc(String actionType);
    
    // Pronalazi sve aktivnosti u vremenskom rasponu
    List<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronalazi sve aktivnosti korisnika
    List<ActivityLog> findByUserId(String userId);
}
