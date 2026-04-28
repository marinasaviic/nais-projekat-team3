package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Team;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/relationships")
public class CollaborationRelationshipController {

    private final CollaborationGraphService collaborationGraphService;

    public CollaborationRelationshipController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @PostMapping("/users/{userId}/teams/{teamId}")
    public Team addUserToTeam(
            @PathVariable String userId,
            @PathVariable String teamId,
            @RequestParam String role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime assignedAt
    ) {
        return collaborationGraphService.addUserToTeam(userId, teamId, role, assignedAt);
    }

    @PutMapping("/users/{userId}/teams/{teamId}")
    public void updateUserTeamRole(
            @PathVariable String userId,
            @PathVariable String teamId,
            @RequestParam String newRole
    ) {
        collaborationGraphService.updateUserTeamRole(userId, teamId, newRole);
    }

    @DeleteMapping("/users/{userId}/teams/{teamId}")
    public void removeUserFromTeam(@PathVariable String userId, @PathVariable String teamId) {
        collaborationGraphService.removeUserFromTeam(userId, teamId);
    }

    @PostMapping("/teams/{teamId}/pricelists/{pricelistId}")
    public Team assignTeamToPricelist(
            @PathVariable String teamId,
            @PathVariable String pricelistId,
            @RequestParam String ownershipType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime assignedAt
    ) {
        return collaborationGraphService.assignTeamToPricelist(teamId, pricelistId, ownershipType, assignedAt);
    }

    @PutMapping("/teams/{teamId}/pricelists/{pricelistId}")
    public void updateTeamPricelistOwnership(
            @PathVariable String teamId,
            @PathVariable String pricelistId,
            @RequestParam String newOwnershipType
    ) {
        collaborationGraphService.updateTeamPricelistOwnership(teamId, pricelistId, newOwnershipType);
    }

    @DeleteMapping("/teams/{teamId}/pricelists/{pricelistId}")
    public void unassignTeamFromPricelist(@PathVariable String teamId, @PathVariable String pricelistId) {
        collaborationGraphService.unassignTeamFromPricelist(teamId, pricelistId);
    }

    @PostMapping("/pricelists/{pricelistId}/regions/{regionId}")
    public Pricelist connectPricelistToRegion(
            @PathVariable String pricelistId,
            @PathVariable String regionId,
            @RequestParam String coverageLevel
    ) {
        return collaborationGraphService.connectPricelistToRegion(pricelistId, regionId, coverageLevel);
    }

    @PutMapping("/pricelists/{pricelistId}/regions/{regionId}")
    public void updatePricelistRegionCoverage(
            @PathVariable String pricelistId,
            @PathVariable String regionId,
            @RequestParam String newCoverageLevel
    ) {
        collaborationGraphService.updatePricelistRegionCoverage(pricelistId, regionId, newCoverageLevel);
    }

    @DeleteMapping("/pricelists/{pricelistId}/regions/{regionId}")
    public void disconnectPricelistFromRegion(@PathVariable String pricelistId, @PathVariable String regionId) {
        collaborationGraphService.disconnectPricelistFromRegion(pricelistId, regionId);
    }

    @PostMapping("/users/{userId}/pricelists/{pricelistId}/actions")
    public void logUserActionOnPricelist(
            @PathVariable String userId,
            @PathVariable String pricelistId,
            @RequestParam String actionType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime timestamp,
            @RequestParam Integer durationMinutes
    ) {
        collaborationGraphService.logUserActionOnPricelist(userId, pricelistId, actionType, timestamp, durationMinutes);
    }

    @DeleteMapping("/users/{userId}/pricelists/{pricelistId}/actions")
    public void deleteUserActionOnPricelist(
            @PathVariable String userId,
            @PathVariable String pricelistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime timestamp
    ) {
        collaborationGraphService.deleteUserActionOnPricelist(userId, pricelistId, timestamp);
    }
}
