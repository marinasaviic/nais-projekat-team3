package rs.ac.uns.acs.nais.SalesProcessTrackingService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Customer;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesProcess;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesRepresentative;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Stage;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.SalesProcessRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.SalesRepresentativeRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.StageRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class GraphSalesService {

    private final CustomerRepository customerRepository;
    private final StageRepository stageRepository;
    private final SalesRepresentativeRepository salesRepresentativeRepository;
    private final SalesProcessRepository salesProcessRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String salesAnalyticsUrl;

    public GraphSalesService(
            CustomerRepository customerRepository,
            StageRepository stageRepository,
            SalesRepresentativeRepository salesRepresentativeRepository,
            SalesProcessRepository salesProcessRepository,
            @Value("${sales-analytics.url:http://sales-analytics-service:8080/sales-analytics}") String salesAnalyticsUrl
    ) {
        this.customerRepository = customerRepository;
        this.stageRepository = stageRepository;
        this.salesRepresentativeRepository = salesRepresentativeRepository;
        this.salesProcessRepository = salesProcessRepository;
        this.salesAnalyticsUrl = salesAnalyticsUrl;
    }

    // CUSTOMER CRUD

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(String id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);
        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setCity(updatedCustomer.getCity());
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    // STAGE CRUD

    public Stage createStage(Stage stage) {
        return stageRepository.save(stage);
    }

    public List<Stage> getAllStages() {
        return stageRepository.findAll();
    }

    public Stage getStageById(String id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stage not found with id: " + id));
    }

    public Stage updateStage(String id, Stage updatedStage) {
        Stage existingStage = getStageById(id);
        existingStage.setName(updatedStage.getName());
        return stageRepository.save(existingStage);
    }

    public void deleteStage(String id) {
        stageRepository.deleteById(id);
    }

    // SALES REPRESENTATIVE CRUD

    public SalesRepresentative createSalesRepresentative(SalesRepresentative representative) {
        return salesRepresentativeRepository.save(representative);
    }

    public List<SalesRepresentative> getAllSalesRepresentatives() {
        return salesRepresentativeRepository.findAll();
    }

    public SalesRepresentative getSalesRepresentativeById(String id) {
        return salesRepresentativeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sales representative not found with id: " + id));
    }

    public SalesRepresentative updateSalesRepresentative(String id, SalesRepresentative updatedRepresentative) {
        SalesRepresentative existingRepresentative = getSalesRepresentativeById(id);
        existingRepresentative.setName(updatedRepresentative.getName());
        return salesRepresentativeRepository.save(existingRepresentative);
    }

    public void deleteSalesRepresentative(String id) {
        salesRepresentativeRepository.deleteById(id);
    }

    // SALES PROCESS CRUD

    public SalesProcess createSalesProcess(SalesProcess salesProcess) {
        return salesProcessRepository.save(salesProcess);
    }

    public List<SalesProcess> getAllSalesProcesses() {
        return salesProcessRepository.findAll();
    }

    public SalesProcess getSalesProcessById(String id) {
        return salesProcessRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sales process not found with id: " + id));
    }

    public SalesProcess updateSalesProcess(String id, SalesProcess updatedSalesProcess) {
        SalesProcess existingSalesProcess = getSalesProcessById(id);
        existingSalesProcess.setTitle(updatedSalesProcess.getTitle());
        existingSalesProcess.setStatus(updatedSalesProcess.getStatus());
        return salesProcessRepository.save(existingSalesProcess);
    }

    public void deleteSalesProcess(String id) {
        salesProcessRepository.deleteById(id);
    }

    // RELATIONSHIPS

    public SalesProcess connectCustomerToProcess(String customerId, String processId) {
        getCustomerById(customerId);
        getSalesProcessById(processId);
        return salesProcessRepository.connectCustomerToProcess(customerId, processId);
    }

    public SalesProcess connectRepresentativeToProcess(String representativeId, String processId) {
        getSalesRepresentativeById(representativeId);
        getSalesProcessById(processId);
        return salesProcessRepository.connectRepresentativeToProcess(representativeId, processId);
    }

    public SalesProcess setCurrentStage(String processId, String stageId) {
        SalesProcess process = getSalesProcessById(processId);
        Stage targetStage = getStageById(stageId);

        String previousStageId = salesProcessRepository.findCurrentStageId(processId);
        String previousStageName = salesProcessRepository.findCurrentStageName(processId);

        SalesProcess updatedProcess = setCurrentStageOnly(processId, stageId);

        try {
            publishStageTransitionEvent(process, previousStageName, targetStage);
        } catch (RuntimeException ex) {
            compensateStageTransition(processId, previousStageId, ex);
        }

        return updatedProcess;
    }

    private SalesProcess setCurrentStageOnly(String processId, String stageId) {
        try {
            salesProcessRepository.removeCurrentStageRelation(processId);
        } catch (Exception ignored) {
        }

        return salesProcessRepository.setCurrentStage(processId, stageId);
    }

    private void publishStageTransitionEvent(SalesProcess process, String previousStageName, Stage targetStage) {
        Map<String, Object> event = buildStageTransitionEvent(process, previousStageName, targetStage);

        try {
            ResponseEntity<Boolean> response = restTemplate.postForEntity(salesAnalyticsUrl, event, Boolean.class);
            if (!Boolean.TRUE.equals(response.getBody())) {
                throw new IllegalStateException("Sales analytics service rejected stage transition event");
            }
        } catch (RestClientException ex) {
            throw new IllegalStateException("Sales analytics service is unavailable", ex);
        }
    }

    private Map<String, Object> buildStageTransitionEvent(SalesProcess process, String previousStageName, Stage targetStage) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("opportunityId", process.getId() + "-STAGE-" + System.currentTimeMillis());
        event.put("customerId", valueOrDefault(salesProcessRepository.findCustomerIdForProcess(process.getId()), "UNKNOWN_CUSTOMER"));
        event.put("customerSegment", "Pharma CRM");
        event.put("salesRepId", valueOrDefault(salesProcessRepository.findSalesRepresentativeIdForProcess(process.getId()), "UNKNOWN_REP"));
        event.put("salesRepName", valueOrDefault(salesProcessRepository.findSalesRepresentativeNameForProcess(process.getId()), "Unknown representative"));
        event.put("region", valueOrDefault(salesProcessRepository.findCustomerCityForProcess(process.getId()), "Unknown region"));
        event.put("productCategory", "Sales process");
        event.put("stageFrom", valueOrDefault(previousStageName, "No previous stage"));
        event.put("stageTo", targetStage.getName());
        event.put("activityType", "Stage Transition");
        event.put("outcome", "Stage changed");
        event.put("dealValue", 10000.0);
        event.put("probability", resolveProbability(targetStage.getName()));
        event.put("stageDurationHours", 24.0);
        event.put("activityDurationMinutes", 5.0);
        event.put("timestamp", Instant.now().toString());
        return event;
    }

    private Double resolveProbability(String stageName) {
        if (stageName == null) {
            return 0.10;
        }
        return switch (stageName) {
            case "Lead Qualification", "Qualification" -> 0.20;
            case "Needs Analysis", "Proposal" -> 0.40;
            case "Offer Sent", "Negotiation" -> 0.65;
            case "Closed Won" -> 1.00;
            case "Closed Lost" -> 0.00;
            default -> 0.50;
        };
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    private void compensateStageTransition(String processId, String previousStageId, RuntimeException cause) {
        try {
            salesProcessRepository.removeCurrentStageRelation(processId);
            if (previousStageId != null && !previousStageId.isBlank()) {
                salesProcessRepository.setCurrentStage(processId, previousStageId);
            }
        } catch (Exception compensationException) {
            cause.addSuppressed(compensationException);
        }

        throw new IllegalStateException("Stage transition was rolled back because analytics event could not be saved", cause);
    }

    public void removeCustomerProcessRelation(String customerId, String processId) {
        salesProcessRepository.removeCustomerProcessRelation(customerId, processId);
    }

    public void removeRepresentativeProcessRelation(String representativeId, String processId) {
        salesProcessRepository.removeRepresentativeProcessRelation(representativeId, processId);
    }

    public void removeCurrentStageRelation(String processId) {
        salesProcessRepository.removeCurrentStageRelation(processId);
    }

    public Stage connectStageToStage(String fromStageId, String toStageId) {
        getStageById(fromStageId);
        getStageById(toStageId);
        return stageRepository.connectStageToStage(fromStageId, toStageId);

    }

    public Stage removeStageToStageRelation(String fromStageId, String toStageId) {
        getStageById(fromStageId);
        getStageById(toStageId);
        return stageRepository.removeStageToStageRelation(fromStageId, toStageId);

    }

    // QUERIES

    public List<String> countProcessesByStage() {
        return salesProcessRepository.countProcessesByStage();
    }

    public List<String> countProcessesByRepresentative() {
        return salesProcessRepository.countProcessesByRepresentative();
    }

    public List<String> findCustomersWithMultipleProcesses() {
        return salesProcessRepository.findCustomersWithMultipleProcesses();
    }

    public List<String> findAllowedTransitions(String stageName) {
        return salesProcessRepository.findAllowedTransitions(stageName);
    }

    public List<String> countProcessesByStatusAndStage() {
        return salesProcessRepository.countProcessesByStatusAndStage();
    }

    public List<String> findActiveProcessesWithCustomerRepresentativeAndStage() {
        return salesProcessRepository.findActiveProcessesWithCustomerRepresentativeAndStage();
    }

    public List<String> countProcessesByRepresentativeCityAndStage() {
        return salesProcessRepository.countProcessesByRepresentativeCityAndStage();
    }

    public List<String> findStagePathsFromQualification() {
        return salesProcessRepository.findStagePathsFromQualification();
    }

    public List<String> findProcessesInImportantSalesStages() {
        return salesProcessRepository.findProcessesInImportantSalesStages();
    }
}