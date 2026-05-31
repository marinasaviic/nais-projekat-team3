package rs.ac.uns.acs.nais.AdverseEffectsSearchService.model;

import java.time.LocalDate;

public class AdverseEventReportDocument {
    private String id;
    private String drugId;
    private String drugName;
    private String activeSubstance;
    private String reactionType;
    private String severity;
    private String patientAgeGroup;
    private int patientAge;
    private String region;
    private String reporterType;
    private String description;
    private LocalDate eventDate;
    private boolean hospitalizationRequired;
    private double outcomeScore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPatientAgeGroup() {
        return patientAgeGroup;
    }

    public void setPatientAgeGroup(String patientAgeGroup) {
        this.patientAgeGroup = patientAgeGroup;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getReporterType() {
        return reporterType;
    }

    public void setReporterType(String reporterType) {
        this.reporterType = reporterType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isHospitalizationRequired() {
        return hospitalizationRequired;
    }

    public void setHospitalizationRequired(boolean hospitalizationRequired) {
        this.hospitalizationRequired = hospitalizationRequired;
    }

    public double getOutcomeScore() {
        return outcomeScore;
    }

    public void setOutcomeScore(double outcomeScore) {
        this.outcomeScore = outcomeScore;
    }
}
