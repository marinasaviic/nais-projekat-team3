package rs.ac.uns.acs.nais.CollaborativePricelistService.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;
import rs.ac.uns.acs.nais.CollaborativePricelistService.service.CollaborationGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final CollaborationGraphService collaborationGraphService;

    public ActivityLogController(CollaborationGraphService collaborationGraphService) {
        this.collaborationGraphService = collaborationGraphService;
    }

    @PostMapping
    public ActivityLog createActivityLog(@RequestBody ActivityLog activityLog) {
        return collaborationGraphService.createActivityLog(activityLog);
    }

    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return collaborationGraphService.getAllActivityLogs();
    }

    @GetMapping("/{id}")
    public ActivityLog getActivityLogById(@PathVariable String id) {
        return collaborationGraphService.getActivityLogById(id);
    }

    @PutMapping("/{id}")
    public ActivityLog updateActivityLog(@PathVariable String id, @RequestBody ActivityLog activityLog) {
        return collaborationGraphService.updateActivityLog(id, activityLog);
    }

    @DeleteMapping("/{id}")
    public void deleteActivityLog(@PathVariable String id) {
        collaborationGraphService.deleteActivityLog(id);
    }
}
