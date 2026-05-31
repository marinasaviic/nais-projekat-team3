package rs.ac.uns.acs.nais.AdverseEffectsSearchService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.DrugDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.ElasticsearchDocumentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ElasticsearchDocumentService elasticsearch;
    private final ObjectMapper objectMapper;

    private final List<String> manufacturers = List.of("Galenika", "Hemofarm", "PharmaSwiss", "Bayer", "Pfizer");
    private final List<String> classes = List.of("Analgetik", "Antibiotik", "Antihipertenziv", "Antikoagulans", "Antidepresiv");
    private final List<String> substances = List.of("ibuprofen", "amoksicilin", "amlodipin", "varfarin", "sertralin");
    private final List<String> sideEffects = List.of("mucnina", "osip", "vrtoglavica", "umor", "glavobolja", "otok", "nesanica");
    private final List<String> severities = List.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
    private final List<String> regions = List.of("Beograd", "Novi Sad", "Nis", "Kragujevac", "Subotica");
    private final List<String> reporters = List.of("doctor", "pharmacist", "patient", "wholesale_customer");

    public DataSeeder(ElasticsearchDocumentService elasticsearch, ObjectMapper objectMapper) {
        this.elasticsearch = elasticsearch;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        waitForElasticsearch();
        elasticsearch.forceCreateIndex(ElasticsearchDocumentService.DRUGS_INDEX, elasticsearch.drugIndexMapping());
        elasticsearch.forceCreateIndex(ElasticsearchDocumentService.REPORTS_INDEX, elasticsearch.reportIndexMapping());

        seedDrugs();
        seedReports();
    }

    private void waitForElasticsearch() throws Exception {
        int maxAttempts = 30;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                elasticsearch.count(ElasticsearchDocumentService.DRUGS_INDEX);
                return;
            } catch (Exception ex) {
                if (attempt == maxAttempts) {
                    throw ex;
                }
                Thread.sleep(2000);
            }
        }
    }

    private void seedDrugs() throws Exception {
        StringBuilder bulk = new StringBuilder();
        for (int i = 1; i <= 1000; i++) {
            DrugDocument drug = new DrugDocument();
            drug.setId("drug-" + i);
            drug.setName("Lek " + i + " " + substances.get(i % substances.size()));
            drug.setActiveSubstance(substances.get(i % substances.size()));
            drug.setManufacturer(manufacturers.get(i % manufacturers.size()));
            drug.setTherapeuticClass(classes.get(i % classes.size()));
            drug.setPrescriptionType(i % 4 == 0 ? "OTC" : "RX");
            drug.setCommonSideEffects(List.of(sideEffects.get(i % sideEffects.size()), sideEffects.get((i + 2) % sideEffects.size())));
            drug.setRiskScore(1 + (i % 90) / 10.0);
            drug.setReportedCases(15 + (i * 7) % 900);
            drug.setDescription("Lek se koristi u terapiji za " + drug.getTherapeuticClass()
                    + ". Opis leka sadrzi moguce nezeljene efekte kao sto su "
                    + String.join(", ", drug.getCommonSideEffects())
                    + " i napomene za pracenje bezbednosti pacijenata.");
            appendBulkIndex(bulk, ElasticsearchDocumentService.DRUGS_INDEX, drug.getId(), drug);
        }
        elasticsearch.bulk(bulk.toString());
    }

    private void seedReports() throws Exception {
        StringBuilder bulk = new StringBuilder();
        LocalDate baseDate = LocalDate.now().minusDays(730);
        for (int i = 1; i <= 1000; i++) {
            String substance = substances.get(i % substances.size());
            AdverseEventReportDocument report = new AdverseEventReportDocument();
            report.setId("report-" + i);
            report.setDrugId("drug-" + ((i % 1000) + 1));
            report.setDrugName("Lek " + ((i % 1000) + 1) + " " + substance);
            report.setActiveSubstance(substance);
            report.setReactionType(sideEffects.get((i + 1) % sideEffects.size()));
            report.setSeverity(severities.get(i % severities.size()));
            report.setPatientAge(18 + (i * 3) % 70);
            report.setPatientAgeGroup(ageGroup(report.getPatientAge()));
            report.setRegion(regions.get(i % regions.size()));
            report.setReporterType(reporters.get(i % reporters.size()));
            report.setEventDate(baseDate.plusDays(i % 730));
            report.setHospitalizationRequired(i % 11 == 0 || "CRITICAL".equals(report.getSeverity()));
            report.setOutcomeScore(1 + (i % 50) / 10.0);
            report.setDescription("Prijavljen je nezeljeni efekat: " + report.getReactionType()
                    + " nakon primene leka " + report.getDrugName()
                    + ". Opis prijave sadrzi simptome, trajanje reakcije i procenu ozbiljnosti: "
                    + report.getSeverity() + ".");
            appendBulkIndex(bulk, ElasticsearchDocumentService.REPORTS_INDEX, report.getId(), report);
        }
        elasticsearch.bulk(bulk.toString());
    }

    private String ageGroup(int age) {
        if (age < 30) {
            return "18-29";
        }
        if (age < 45) {
            return "30-44";
        }
        if (age < 65) {
            return "45-64";
        }
        return "65+";
    }

    private void appendBulkIndex(StringBuilder bulk, String index, String id, Object document) throws Exception {
        bulk.append(objectMapper.writeValueAsString(Map.of("index", Map.of("_index", index, "_id", id)))).append('\n');
        bulk.append(objectMapper.writeValueAsString(document)).append('\n');
    }
}
