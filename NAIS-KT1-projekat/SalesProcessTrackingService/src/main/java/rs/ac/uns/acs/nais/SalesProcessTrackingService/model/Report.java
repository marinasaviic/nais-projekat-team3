package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Node("Report")
public class Report {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate reportDate;
    private LocalDate symptomDate;
    private String status;
    private String severity;
    private String description;
    private String analysisConclusion;

    public Report() {
    }

    public Report(LocalDate reportDate, LocalDate symptomDate, String status, String severity, String description, String analysisConclusion) {
        this.reportDate = reportDate;
        this.symptomDate = symptomDate;
        this.status = status;
        this.severity = severity;
        this.description = description;
        this.analysisConclusion = analysisConclusion;
    }

    public Long getId() { return id; }
    public LocalDate getReportDate() { return reportDate; }
    public LocalDate getSymptomDate() { return symptomDate; }
    public String getStatus() { return status; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getAnalysisConclusion() { return analysisConclusion; }

    public void setId(Long id) { this.id = id; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public void setSymptomDate(LocalDate symptomDate) { this.symptomDate = symptomDate; }
    public void setStatus(String status) { this.status = status; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setDescription(String description) { this.description = description; }
    public void setAnalysisConclusion(String analysisConclusion) { this.analysisConclusion = analysisConclusion; }
}