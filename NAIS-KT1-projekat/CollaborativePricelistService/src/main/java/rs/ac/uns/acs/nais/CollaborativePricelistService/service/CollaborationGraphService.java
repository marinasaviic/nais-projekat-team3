package rs.ac.uns.acs.nais.CollaborativePricelistService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Region;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Team;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.TeamUser;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.ActivityLogRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.CollaborationRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.PricelistRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.RegionRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.TeamRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.TeamUserRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CollaborationGraphService {

    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final PricelistRepository pricelistRepository;
    private final RegionRepository regionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final CollaborationRepository collaborationRepository;

    public CollaborationGraphService(
            TeamUserRepository teamUserRepository,
            TeamRepository teamRepository,
            PricelistRepository pricelistRepository,
            RegionRepository regionRepository,
            ActivityLogRepository activityLogRepository,
            CollaborationRepository collaborationRepository
    ) {
        this.teamUserRepository = teamUserRepository;
        this.teamRepository = teamRepository;
        this.pricelistRepository = pricelistRepository;
        this.regionRepository = regionRepository;
        this.activityLogRepository = activityLogRepository;
        this.collaborationRepository = collaborationRepository;
    }

    // TEAM USER CRUD

    public TeamUser createTeamUser(TeamUser teamUser) {
        return teamUserRepository.save(teamUser);
    }

    public List<TeamUser> getAllTeamUsers() {
        return teamUserRepository.findAll();
    }

    public TeamUser getTeamUserById(String id) {
        return teamUserRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team user not found with id: " + id));
    }

    public TeamUser updateTeamUser(String id, TeamUser updatedTeamUser) {
        TeamUser existingUser = getTeamUserById(id);
        existingUser.setName(updatedTeamUser.getName());
        existingUser.setEmail(updatedTeamUser.getEmail());
        existingUser.setPosition(updatedTeamUser.getPosition());
        return teamUserRepository.save(existingUser);
    }

    public void deleteTeamUser(String id) {
        teamUserRepository.deleteById(id);
    }

    // TEAM CRUD

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(String id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team not found with id: " + id));
    }

    public Team updateTeam(String id, Team updatedTeam) {
        Team existingTeam = getTeamById(id);
        existingTeam.setName(updatedTeam.getName());
        existingTeam.setType(updatedTeam.getType());
        return teamRepository.save(existingTeam);
    }

    public void deleteTeam(String id) {
        teamRepository.deleteById(id);
    }

    // PRICELIST CRUD

    public Pricelist createPricelist(Pricelist pricelist) {
        return pricelistRepository.save(pricelist);
    }

    public List<Pricelist> getAllPricelists() {
        return pricelistRepository.findAll();
    }

    public Pricelist getPricelistById(String id) {
        return pricelistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pricelist not found with id: " + id));
    }

    public Pricelist updatePricelist(String id, Pricelist updatedPricelist) {
        Pricelist existingPricelist = getPricelistById(id);
        existingPricelist.setName(updatedPricelist.getName());
        existingPricelist.setStatus(updatedPricelist.getStatus());
        existingPricelist.setVersion(updatedPricelist.getVersion());
        return pricelistRepository.save(existingPricelist);
    }

    public void deletePricelist(String id) {
        pricelistRepository.deleteById(id);
    }

    // REGION CRUD

    public Region createRegion(Region region) {
        return regionRepository.save(region);
    }

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Region getRegionById(String id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Region not found with id: " + id));
    }

    public Region updateRegion(String id, Region updatedRegion) {
        Region existingRegion = getRegionById(id);
        existingRegion.setName(updatedRegion.getName());
        existingRegion.setCountry(updatedRegion.getCountry());
        return regionRepository.save(existingRegion);
    }

    public void deleteRegion(String id) {
        regionRepository.deleteById(id);
    }

    // ACTIVITY LOG CRUD

    public ActivityLog createActivityLog(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    public ActivityLog getActivityLogById(String id) {
        return activityLogRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity log not found with id: " + id));
    }

    public ActivityLog updateActivityLog(String id, ActivityLog updatedActivityLog) {
        ActivityLog existingLog = getActivityLogById(id);
        existingLog.setActionType(updatedActivityLog.getActionType());
        existingLog.setTimestamp(updatedActivityLog.getTimestamp());
        existingLog.setDurationMinutes(updatedActivityLog.getDurationMinutes());
        existingLog.setDetails(updatedActivityLog.getDetails());
        existingLog.setUserId(updatedActivityLog.getUserId());
        existingLog.setTeamId(updatedActivityLog.getTeamId());
        existingLog.setPricelistId(updatedActivityLog.getPricelistId());
        existingLog.setRegionId(updatedActivityLog.getRegionId());
        return activityLogRepository.save(existingLog);
    }

    public void deleteActivityLog(String id) {
        activityLogRepository.deleteById(id);
    }

    // RELATIONSHIP CRUD

    public Team addUserToTeam(String userId, String teamId, String role, ZonedDateTime assignedAt) {
        getTeamUserById(userId);
        getTeamById(teamId);
        return collaborationRepository.addUserToTeam(userId, teamId, role, assignedAt);
    }

    public void updateUserTeamRole(String userId, String teamId, String newRole) {
        getTeamUserById(userId);
        getTeamById(teamId);
        collaborationRepository.updateUserTeamRole(userId, teamId, newRole);
    }

    public void removeUserFromTeam(String userId, String teamId) {
        collaborationRepository.removeUserFromTeam(userId, teamId);
    }

    public Team assignTeamToPricelist(String teamId, String pricelistId, String ownershipType, ZonedDateTime assignedAt) {
        getTeamById(teamId);
        getPricelistById(pricelistId);
        return collaborationRepository.assignTeamToPricelist(teamId, pricelistId, ownershipType, assignedAt);
    }

    public void updateTeamPricelistOwnership(String teamId, String pricelistId, String newOwnershipType) {
        getTeamById(teamId);
        getPricelistById(pricelistId);
        collaborationRepository.updateTeamPricelistOwnership(teamId, pricelistId, newOwnershipType);
    }

    public void unassignTeamFromPricelist(String teamId, String pricelistId) {
        collaborationRepository.unassignTeamFromPricelist(teamId, pricelistId);
    }

    public Pricelist connectPricelistToRegion(String pricelistId, String regionId, String coverageLevel) {
        getPricelistById(pricelistId);
        getRegionById(regionId);
        return collaborationRepository.connectPricelistToRegion(pricelistId, regionId, coverageLevel);
    }

    public void updatePricelistRegionCoverage(String pricelistId, String regionId, String newCoverageLevel) {
        getPricelistById(pricelistId);
        getRegionById(regionId);
        collaborationRepository.updatePricelistRegionCoverage(pricelistId, regionId, newCoverageLevel);
    }

    public void disconnectPricelistFromRegion(String pricelistId, String regionId) {
        collaborationRepository.disconnectPricelistFromRegion(pricelistId, regionId);
    }

    public void logUserActionOnPricelist(String userId, String pricelistId, String actionType, ZonedDateTime timestamp, Integer durationMinutes) {
        getTeamUserById(userId);
        getPricelistById(pricelistId);
        collaborationRepository.logUserActionOnPricelist(userId, pricelistId, actionType, timestamp, durationMinutes);
    }

    public void deleteUserActionOnPricelist(String userId, String pricelistId, ZonedDateTime timestamp) {
        collaborationRepository.deleteUserActionOnPricelist(userId, pricelistId, timestamp);
    }

    // QUERIES

    public List<String> teamsWorkingOnMostPricelists() {
        return collaborationRepository.teamsWorkingOnMostPricelists();
    }

    public List<String> regionsWithMostActivePricelists() {
        return collaborationRepository.regionsWithMostActivePricelists();
    }

    public List<String> usersAssignedToMultipleTeams() {
        return collaborationRepository.usersAssignedToMultipleTeams();
    }

    public List<String> teamsWithMostMembers() {
        return collaborationRepository.teamsWithMostMembers();
    }

    public List<String> pricelistsWithoutAssignedTeams() {
        return collaborationRepository.pricelistsWithoutAssignedTeams();
    }

    public List<String> usersWithMostChanges() {
        return collaborationRepository.usersWithMostChanges();
    }

    public List<String> averageWorkTimeByTeam() {
        return collaborationRepository.averageWorkTimeByTeam();
    }

    public List<String> activitiesInLastNDays(Integer days) {
        return collaborationRepository.activitiesInLastNDays(days);
    }

    public List<String> mostActiveUserPerRegion() {
        return collaborationRepository.mostActiveUserPerRegion();
    }

    // ============================================================================
    // FZ 2.4.1: Praćenje aktivnosti timova - PERFORMANCE EVALUATION METHODS
    // ============================================================================

    public List<String> teamPerformanceDashboard() {
        return collaborationRepository.teamPerformanceDashboard();
    }

    public String getTeamPerformanceMetrics(String teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + teamId));
        return collaborationRepository.getTeamPerformanceMetrics(teamId);
    }

    public List<String> getTeamActivitySummaryByPeriod(String teamId, Integer days) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + teamId));
        return collaborationRepository.getTeamActivitySummaryByPeriod(teamId, days);
    }

    public List<String> getUserActionCountByTeamAndType() {
        return collaborationRepository.getUserActionCountByTeamAndType();
    }

    public List<String> getTeamPricelistActivityInPeriod(Integer days) {
        return collaborationRepository.getTeamPricelistActivityInPeriod(days);
    }

    public List<String> getTeamProductivityMetrics() {
        return collaborationRepository.getTeamProductivityMetrics();
    }
}
