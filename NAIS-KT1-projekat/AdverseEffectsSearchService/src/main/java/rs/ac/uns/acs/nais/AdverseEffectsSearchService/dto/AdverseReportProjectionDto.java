package rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto;

import java.time.Instant;

public class AdverseReportProjectionDto {
    private String reportId;
    private String drugName;
    private String reactionType;
    private String severity;
    private String region;
    private String reporterType;
    private String hospitalizationRequired;
    private Instant indexedAt;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
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

    public String getHospitalizationRequired() {
        return hospitalizationRequired;
    }

    public void setHospitalizationRequired(String hospitalizationRequired) {
        this.hospitalizationRequired = hospitalizationRequired;
    }

    public Instant getIndexedAt() {
        return indexedAt;
    }

    public void setIndexedAt(Instant indexedAt) {
        this.indexedAt = indexedAt;
    }
}
