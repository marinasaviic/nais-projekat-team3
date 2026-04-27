package rs.ac.uns.acs.nais.CollaborativePricelistService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.ActivityLogRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.CollaborationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service za praćenje aktivnosti vlasnika cenovnika.
 * Implementira FZ 2.4.1: Praćenje aktivnosti timova
 */
@Service
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;
    private final CollaborationRepository collaborationRepository;

    public ActivityService(ActivityLogRepository activityLogRepository, CollaborationRepository collaborationRepository) {
        this.activityLogRepository = activityLogRepository;
        this.collaborationRepository = collaborationRepository;
    }

    /**
     * Logira akciju vlasnika cenovnika na sistemu.
     * Akcija se zapisuje u ActivityLog i kreira PERFORMED relationship.
     *
     * @param userId ID korisnika koji izvršava akciju
     * @param pricelistId ID cenovnika na kojem se akcija izvršava
     * @param teamId ID tima kojem pripada akcija
     * @param actionType Tip akcije (CREATE, UPDATE, DELETE, PUBLISH, ARCHIVE, ACTIVATE, DEACTIVATE)
     * @param durationMinutes Trajanje akcije u minutama
     * @param details Dodatni opis akcije
     * @return Kreirani ActivityLog
     */
    public ActivityLog logUserAction(String userId, String pricelistId, String teamId, 
                                     String actionType, Integer durationMinutes, String details) {
        LocalDateTime timestamp = LocalDateTime.now();
        
        // Kreiraj ActivityLog zapis
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserId(userId);
        activityLog.setPricelistId(pricelistId);
        activityLog.setTeamId(teamId);
        activityLog.setActionType(actionType);
        activityLog.setTimestamp(timestamp);
        activityLog.setDurationMinutes(durationMinutes);
        activityLog.setDetails(details);
        
        ActivityLog saved = activityLogRepository.save(activityLog);
        
        // Kreiraj PERFORMED relationship
        collaborationRepository.logUserActionOnPricelist(userId, pricelistId, actionType, timestamp, durationMinutes);
        
        return saved;
    }

    /**
     * Vraća sve aktivnosti korisnika u zadanom vremenskom periodu.
     */
    public List<ActivityLog> getUserActivities(String userId, Integer daysBack) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(daysBack);
        return activityLogRepository.findByUserIdAndTimestampAfterOrderByTimestampDesc(userId, startDate);
    }

    /**
     * Vraća sve aktivnosti na konkretnom cenovniku.
     */
    public List<ActivityLog> getPricelistActivities(String pricelistId) {
        return activityLogRepository.findByPricelistIdOrderByTimestampDesc(pricelistId);
    }

    /**
     * Vraća sve aktivnosti konkretnog tima.
     */
    public List<ActivityLog> getTeamActivities(String teamId) {
        return activityLogRepository.findByTeamIdOrderByTimestampDesc(teamId);
    }

    /**
     * Vraća sve aktivnosti određenog tipa (npr. CREATE, UPDATE, DELETE).
     */
    public List<ActivityLog> getActivitiesByActionType(String actionType) {
        return activityLogRepository.findByActionTypeOrderByTimestampDesc(actionType);
    }

    /**
     * Vraća sve aktivnosti u zadanom vremenskom periodu.
     */
    public List<ActivityLog> getActivitiesInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
    }

    /**
     * Računa ukupno vrijeme provedeno na radu sa cenovnicima za korisnika.
     */
    public Integer getTotalWorkTimeForUser(String userId) {
        List<ActivityLog> activities = activityLogRepository.findByUserId(userId);
        return activities.stream()
                .mapToInt(a -> a.getDurationMinutes() != null ? a.getDurationMinutes() : 0)
                .sum();
    }

    /**
     * Računa broj akcija po tipu za specifician korisnik.
     */
    public long countActionsByType(String userId, String actionType) {
        List<ActivityLog> activities = activityLogRepository.findByUserId(userId);
        return activities.stream()
                .filter(a -> actionType.equals(a.getActionType()))
                .count();
    }

    /**
     * Vraća sve raspoložive akcije po tipu za analizu.
     */
    public List<String> getAvailableActionTypes() {
        return List.of("CREATE", "UPDATE", "DELETE", "PUBLISH", "ARCHIVE", "ACTIVATE", "DEACTIVATE", "REVIEW", "APPROVE");
    }
}
