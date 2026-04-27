package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.TeamUser;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/team-users")
public class TeamUserController {

    private final CollaborationGraphService collaborationGraphService;

    public TeamUserController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @PostMapping
    public TeamUser createTeamUser(@RequestBody TeamUser teamUser) {
        return collaborationGraphService.createTeamUser(teamUser);
    }

    @GetMapping
    public List<TeamUser> getAllTeamUsers() {
        return collaborationGraphService.getAllTeamUsers();
    }

    @GetMapping("/{id}")
    public TeamUser getTeamUserById(@PathVariable String id) {
        return collaborationGraphService.getTeamUserById(id);
    }

    @PutMapping("/{id}")
    public TeamUser updateTeamUser(@PathVariable String id, @RequestBody TeamUser teamUser) {
        return collaborationGraphService.updateTeamUser(id, teamUser);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamUser(@PathVariable String id) {
        collaborationGraphService.deleteTeamUser(id);
    }
}
