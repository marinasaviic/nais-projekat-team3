package rs.ac.uns.acs.nais.SalesProcessTrackingService.dto;

import java.util.List;
import java.util.Map;

public class SalesPerformanceReportDto {

    private List<String> activeProcesses;
    private List<Map<String, Object>> regionEvents;
    private List<Map<String, Object>> stageBottlenecks;
    private List<Map<String, Object>> weeklyPipelineGrowth;

    public SalesPerformanceReportDto() {
    }

    public SalesPerformanceReportDto(
            List<String> activeProcesses,
            List<Map<String, Object>> regionEvents,
            List<Map<String, Object>> stageBottlenecks,
            List<Map<String, Object>> weeklyPipelineGrowth
    ) {
        this.activeProcesses = activeProcesses;
        this.regionEvents = regionEvents;
        this.stageBottlenecks = stageBottlenecks;
        this.weeklyPipelineGrowth = weeklyPipelineGrowth;
    }

    public List<String> getActiveProcesses() {
        return activeProcesses;
    }

    public void setActiveProcesses(List<String> activeProcesses) {
        this.activeProcesses = activeProcesses;
    }

    public List<Map<String, Object>> getRegionEvents() {
        return regionEvents;
    }

    public void setRegionEvents(List<Map<String, Object>> regionEvents) {
        this.regionEvents = regionEvents;
    }

    public List<Map<String, Object>> getStageBottlenecks() {
        return stageBottlenecks;
    }

    public void setStageBottlenecks(List<Map<String, Object>> stageBottlenecks) {
        this.stageBottlenecks = stageBottlenecks;
    }

    public List<Map<String, Object>> getWeeklyPipelineGrowth() {
        return weeklyPipelineGrowth;
    }

    public void setWeeklyPipelineGrowth(List<Map<String, Object>> weeklyPipelineGrowth) {
        this.weeklyPipelineGrowth = weeklyPipelineGrowth;
    }
}