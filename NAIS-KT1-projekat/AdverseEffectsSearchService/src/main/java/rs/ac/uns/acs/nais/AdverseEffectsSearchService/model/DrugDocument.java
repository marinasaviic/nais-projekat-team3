package rs.ac.uns.acs.nais.AdverseEffectsSearchService.model;

import java.util.List;

public class DrugDocument {
    private String id;
    private String name;
    private String activeSubstance;
    private String manufacturer;
    private String therapeuticClass;
    private String prescriptionType;
    private String description;
    private List<String> commonSideEffects;
    private double riskScore;
    private int reportedCases;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTherapeuticClass() {
        return therapeuticClass;
    }

    public void setTherapeuticClass(String therapeuticClass) {
        this.therapeuticClass = therapeuticClass;
    }

    public String getPrescriptionType() {
        return prescriptionType;
    }

    public void setPrescriptionType(String prescriptionType) {
        this.prescriptionType = prescriptionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCommonSideEffects() {
        return commonSideEffects;
    }

    public void setCommonSideEffects(List<String> commonSideEffects) {
        this.commonSideEffects = commonSideEffects;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public int getReportedCases() {
        return reportedCases;
    }

    public void setReportedCases(int reportedCases) {
        this.reportedCases = reportedCases;
    }
}
