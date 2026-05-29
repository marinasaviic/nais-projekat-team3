package collab.service;

import org.springframework.stereotype.Service;
import collab.model.ActivityLog;
import collab.model.Pricelist;
import collab.model.Region;
import collab.model.Team;
import collab.model.TeamUser;
import collab.repository.ActivityLogRepository;
import collab.repository.CollaborationRepository;
import collab.repository.PricelistRepository;
import collab.repository.RegionRepository;
import collab.repository.TeamRepository;
import collab.repository.TeamUserRepository;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import collab.cache.CacheService;
import collab.model.Pricelist;

@Service
public class CollaborationGraphService {

    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final PricelistRepository pricelistRepository;
    private final RegionRepository regionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final CollaborationRepository collaborationRepository;
    private final CacheService cacheService;

    public CollaborationGraphService(
            TeamUserRepository teamUserRepository,
            TeamRepository teamRepository,
            PricelistRepository pricelistRepository,
            RegionRepository regionRepository,
            ActivityLogRepository activityLogRepository,
                CollaborationRepository collaborationRepository,
                CacheService cacheService
    ) {
        this.teamUserRepository = teamUserRepository;
        this.teamRepository = teamRepository;
        this.pricelistRepository = pricelistRepository;
        this.regionRepository = regionRepository;
        this.activityLogRepository = activityLogRepository;
        this.collaborationRepository = collaborationRepository;
        this.cacheService = cacheService;
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
        Pricelist saved = pricelistRepository.save(pricelist);
        // Invalidate any region cache entries related to this pricelist (none initially)
        return saved;
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
        Pricelist saved = pricelistRepository.save(existingPricelist);
        // if status changed to Archived, invalidate related region caches
        if ("ARCHIVED".equalsIgnoreCase(saved.getStatus())) {
            java.util.List<String> regions = collaborationRepository.findRegionsForPricelist(id);
            for (String regionId : regions) {
                cacheService.del(regionCacheKey(regionId));
            }
        } else {
            // general invalidation for regions this pricelist is assigned to
            java.util.List<String> regions = collaborationRepository.findRegionsForPricelist(id);
            for (String regionId : regions) {
                cacheService.del(regionCacheKey(regionId));
            }
        }
        return saved;
    }

    public void deletePricelist(String id) {
        // invalidate caches for regions the pricelist belonged to
        java.util.List<String> regions = collaborationRepository.findRegionsForPricelist(id);
        for (String regionId : regions) {
            cacheService.del(regionCacheKey(regionId));
        }
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
        collaborationRepository.addUserToTeam(userId, teamId, role, assignedAt);
        return getTeamById(teamId);
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
        collaborationRepository.assignTeamToPricelist(teamId, pricelistId, ownershipType, assignedAt);
        return getTeamById(teamId);
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
        collaborationRepository.connectPricelistToRegion(pricelistId, regionId, coverageLevel);
        // invalidate cache for that region
        cacheService.del(regionCacheKey(regionId));
        return getPricelistById(pricelistId);
    }

    public void updatePricelistRegionCoverage(String pricelistId, String regionId, String newCoverageLevel) {
        getPricelistById(pricelistId);
        getRegionById(regionId);
        collaborationRepository.updatePricelistRegionCoverage(pricelistId, regionId, newCoverageLevel);
        cacheService.del(regionCacheKey(regionId));
    }

    public void disconnectPricelistFromRegion(String pricelistId, String regionId) {
        collaborationRepository.disconnectPricelistFromRegion(pricelistId, regionId);
        cacheService.del(regionCacheKey(regionId));
    }

    private String regionCacheKey(String regionId) {
        return "region:" + regionId + ":pricelists";
    }

    public List<Pricelist> getPricelistsForRegion(String regionId) {
        String key = regionCacheKey(regionId);
        java.util.Optional<Pricelist[]> cached = cacheService.get(key, Pricelist[].class);
        if (cached.isPresent()) {
            return Arrays.asList(cached.get());
        }
        java.util.List<Pricelist> fetched = collaborationRepository.findActivePricelistsForRegion(regionId);
        // store in cache for 5 minutes
        cacheService.set(key, fetched, Duration.ofMinutes(5));
        return fetched;
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
