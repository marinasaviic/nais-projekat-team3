package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
public class CollaborationQueryController {

    private final CollaborationGraphService collaborationGraphService;

    public CollaborationQueryController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @GetMapping("/teams-most-pricelists")
    public List<String> teamsWorkingOnMostPricelists() {
        return collaborationGraphService.teamsWorkingOnMostPricelists();
    }

    @GetMapping("/regions-most-active-pricelists")
    public List<String> regionsWithMostActivePricelists() {
        return collaborationGraphService.regionsWithMostActivePricelists();
    }

    @GetMapping("/users-multiple-teams")
    public List<String> usersAssignedToMultipleTeams() {
        return collaborationGraphService.usersAssignedToMultipleTeams();
    }

    @GetMapping("/teams-most-members")
    public List<String> teamsWithMostMembers() {
        return collaborationGraphService.teamsWithMostMembers();
    }

    @GetMapping("/pricelists-without-team")
    public List<String> pricelistsWithoutAssignedTeams() {
        return collaborationGraphService.pricelistsWithoutAssignedTeams();
    }

    @GetMapping("/users-most-changes")
    public List<String> usersWithMostChanges() {
        return collaborationGraphService.usersWithMostChanges();
    }

    @GetMapping("/average-work-time-by-team")
    public List<String> averageWorkTimeByTeam() {
        return collaborationGraphService.averageWorkTimeByTeam();
    }

    @GetMapping("/activities-last-days")
    public List<String> activitiesInLastNDays(@RequestParam(defaultValue = "7") Integer days) {
        return collaborationGraphService.activitiesInLastNDays(days);
    }

    @GetMapping("/most-active-user-by-region")
    public List<String> mostActiveUserPerRegion() {
        return collaborationGraphService.mostActiveUserPerRegion();
    }

    // ============================================================================
    // FZ 2.4.1: Praćenje aktivnosti timova - PERFORMANCE EVALUATION ENDPOINTS
    // ============================================================================

    @GetMapping("/performance/teams-dashboard")
    public List<String> teamPerformanceDashboard() {
        return collaborationGraphService.teamPerformanceDashboard();
    }

    @GetMapping("/performance/team/{teamId}/metrics")
    public String getTeamPerformanceMetrics(@org.springframework.web.bind.annotation.PathVariable String teamId) {
        return collaborationGraphService.getTeamPerformanceMetrics(teamId);
    }

    @GetMapping("/performance/team/{teamId}/activity-summary")
    public List<String> getTeamActivitySummaryByPeriod(
            @org.springframework.web.bind.annotation.PathVariable String teamId,
            @RequestParam(defaultValue = "30") Integer days
    ) {
        return collaborationGraphService.getTeamActivitySummaryByPeriod(teamId, days);
    }

    @GetMapping("/performance/user-actions-by-type")
    public List<String> getUserActionCountByTeamAndType() {
        return collaborationGraphService.getUserActionCountByTeamAndType();
    }

    @GetMapping("/performance/team-pricelist-activity")
    public List<String> getTeamPricelistActivityInPeriod(
            @RequestParam(defaultValue = "30") Integer days
    ) {
        return collaborationGraphService.getTeamPricelistActivityInPeriod(days);
    }

    @GetMapping("/performance/team-productivity")
    public List<String> getTeamProductivityMetrics() {
        return collaborationGraphService.getTeamProductivityMetrics();
    }
}
