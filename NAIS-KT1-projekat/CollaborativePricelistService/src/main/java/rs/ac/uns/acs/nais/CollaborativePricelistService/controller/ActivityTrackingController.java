package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.ActivityService;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller za praćenje aktivnosti vlasnika cenovnika.
 * Implementira FZ 2.4.1: Praćenje aktivnosti timova
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityTrackingController {

    private final ActivityService activityService;
    private final CollaborationGraphService collaborationGraphService;

    public ActivityTrackingController(ActivityService activityService, CollaborationGraphService collaborationGraphService) {
        this.activityService = activityService;
        this.collaborationGraphService = collaborationGraphService;
    }

    // ============================================================================
    // ACTIVITY LOGGING ENDPOINTS
    // ============================================================================

    /**
     * Logira akciju vlasnika cenovnika na sistemu.
     * Ova metoda je ključna za FZ 2.4.1 - evidencija svih akcija sa vremenima rada.
     *
     * POST /api/activities/log
     * {
     *   "userId": "user1",
     *   "pricelistId": "pricelist1",
     *   "teamId": "team1",
     *   "actionType": "UPDATE",
     *   "durationMinutes": 30,
     *   "details": "Updated pricing strategy for Q1"
     * }
     */
    @PostMapping("/log")
    public ActivityLog logUserAction(
            @RequestParam String userId,
            @RequestParam String pricelistId,
            @RequestParam String teamId,
            @RequestParam String actionType,
            @RequestParam Integer durationMinutes,
            @RequestParam(required = false) String details
    ) {
        return activityService.logUserAction(userId, pricelistId, teamId, actionType, durationMinutes, details);
    }

    /**
     * Vraća sve dostupne tipove akcija za logiranje.
     * GET /api/activities/available-actions
     */
    @GetMapping("/available-actions")
    public List<String> getAvailableActionTypes() {
        return activityService.getAvailableActionTypes();
    }

    // ============================================================================
    // USER ACTIVITY QUERIES
    // ============================================================================

    /**
     * Vraća sve aktivnosti konkretnog korisnika u zadanom vremenskom periodu.
     * GET /api/activities/user/{userId}?daysBack=7
     */
    @GetMapping("/user/{userId}")
    public List<ActivityLog> getUserActivities(
            @PathVariable String userId,
            @RequestParam(defaultValue = "30") Integer daysBack
    ) {
        return activityService.getUserActivities(userId, daysBack);
    }

    /**
     * Vraća ukupno vrijeme provedeno na radu sa cenovnicima za korisnika.
     * GET /api/activities/user/{userId}/total-work-time
     */
    @GetMapping("/user/{userId}/total-work-time")
    public Integer getTotalWorkTimeForUser(@PathVariable String userId) {
        return activityService.getTotalWorkTimeForUser(userId);
    }

    /**
     * Računa broj akcija po tipu za specifičnog korisnika.
     * GET /api/activities/user/{userId}/actions-by-type?actionType=UPDATE
     */
    @GetMapping("/user/{userId}/actions-by-type")
    public long countActionsByType(
            @PathVariable String userId,
            @RequestParam String actionType
    ) {
        return activityService.countActionsByType(userId, actionType);
    }

    // ============================================================================
    // PRICELIST ACTIVITY QUERIES
    // ============================================================================

    /**
     * Vraća sve aktivnosti na konkretnom cenovniku.
     * GET /api/activities/pricelist/{pricelistId}
     */
    @GetMapping("/pricelist/{pricelistId}")
    public List<ActivityLog> getPricelistActivities(@PathVariable String pricelistId) {
        return activityService.getPricelistActivities(pricelistId);
    }

    // ============================================================================
    // TEAM ACTIVITY QUERIES
    // ============================================================================

    /**
     * Vraća sve aktivnosti konkretnog tima.
     * GET /api/activities/team/{teamId}
     */
    @GetMapping("/team/{teamId}")
    public List<ActivityLog> getTeamActivities(@PathVariable String teamId) {
        return activityService.getTeamActivities(teamId);
    }

    // ============================================================================
    // ACTION TYPE FILTERING
    // ============================================================================

    /**
     * Vraća sve aktivnosti određenog tipa (npr. CREATE, UPDATE, DELETE).
     * GET /api/activities/by-type/UPDATE
     */
    @GetMapping("/by-type/{actionType}")
    public List<ActivityLog> getActivitiesByActionType(@PathVariable String actionType) {
        return activityService.getActivitiesByActionType(actionType);
    }

    // ============================================================================
    // PERIOD FILTERING
    // ============================================================================

    /**
     * Vraća sve aktivnosti u zadanom vremenskom periodu.
     * GET /api/activities/period?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/period")
    public List<ActivityLog> getActivitiesInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return activityService.getActivitiesInPeriod(start, end);
    }

    // ============================================================================
    // TEAM PERFORMANCE DASHBOARD - FZ 2.4.1 EVALUATION QUERIES
    // ============================================================================

    /**
     * Vraća dashboard sa performansom svih timova.
     * Prikazuje: članove, cenovnike, ukupno vrijeme rada
     * GET /api/activities/dashboard/teams
     */
    @GetMapping("/dashboard/teams")
    public List<String> getTeamPerformanceDashboard() {
        return collaborationGraphService.teamPerformanceDashboard();
    }

    /**
     * Vraća detaljne metrike performansi za specifičan tim.
     * GET /api/activities/dashboard/team/{teamId}
     */
    @GetMapping("/dashboard/team/{teamId}")
    public String getTeamPerformanceMetrics(@PathVariable String teamId) {
        return collaborationGraphService.getTeamPerformanceMetrics(teamId);
    }

    /**
     * Vraća sažetak aktivnosti tima u zadanom vremenskom periodu.
     * GET /api/activities/dashboard/team/{teamId}/summary?days=30
     */
    @GetMapping("/dashboard/team/{teamId}/summary")
    public List<String> getTeamActivitySummaryByPeriod(
            @PathVariable String teamId,
            @RequestParam(defaultValue = "30") Integer days
    ) {
        return collaborationGraphService.getTeamActivitySummaryByPeriod(teamId, days);
    }

    /**
     * Vraća broj akcija po tipu za svakog korisnika i tim.
     * GET /api/activities/dashboard/user-actions-by-type
     */
    @GetMapping("/dashboard/user-actions-by-type")
    public List<String> getUserActionCountByTeamAndType() {
        return collaborationGraphService.getUserActionCountByTeamAndType();
    }

    /**
     * Vraća aktivnost timova na cenovnicima u zadanom vremenskom periodu.
     * GET /api/activities/dashboard/team-pricelist-activity?days=30
     */
    @GetMapping("/dashboard/team-pricelist-activity")
    public List<String> getTeamPricelistActivityInPeriod(
            @RequestParam(defaultValue = "30") Integer days
    ) {
        return collaborationGraphService.getTeamPricelistActivityInPeriod(days);
    }

    /**
     * Vraća produktivnost timova - broj izmjena, članove, ukupno vrijeme.
     * GET /api/activities/dashboard/productivity
     */
    @GetMapping("/dashboard/productivity")
    public List<String> getTeamProductivityMetrics() {
        return collaborationGraphService.getTeamProductivityMetrics();
    }

    // ============================================================================
    // ACTIVITY TYPES INFO
    // ============================================================================

    /**
     * Vraća informacije o dostupnim tipovima akcija.
     * GET /api/activities/action-types
     */
    @GetMapping("/action-types")
    public List<String> getActionTypesInfo() {
        return List.of(
                "CREATE - Kreiranje novog cenovnika",
                "UPDATE - Ažuriranje postojećeg cenovnika",
                "DELETE - Brisanje cenovnika",
                "PUBLISH - Objavljenje cenovnika",
                "ARCHIVE - Arhiviranje cenovnika",
                "ACTIVATE - Aktiviranje cenovnika",
                "DEACTIVATE - Deaktiviranje cenovnika",
                "REVIEW - Pregled i analiza cenovnika",
                "APPROVE - Odobravanje izmjena"
        );
    }
}
