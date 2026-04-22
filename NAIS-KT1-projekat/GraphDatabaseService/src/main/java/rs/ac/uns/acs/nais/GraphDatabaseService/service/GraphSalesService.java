package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Customer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SalesProcess;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SalesRepresentative;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Stage;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.SalesProcessRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.SalesRepresentativeRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.StageRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GraphSalesService {

    private final CustomerRepository customerRepository;
    private final StageRepository stageRepository;
    private final SalesRepresentativeRepository salesRepresentativeRepository;
    private final SalesProcessRepository salesProcessRepository;

    public GraphSalesService(
            CustomerRepository customerRepository,
            StageRepository stageRepository,
            SalesRepresentativeRepository salesRepresentativeRepository,
            SalesProcessRepository salesProcessRepository
    ) {
        this.customerRepository = customerRepository;
        this.stageRepository = stageRepository;
        this.salesRepresentativeRepository = salesRepresentativeRepository;
        this.salesProcessRepository = salesProcessRepository;
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
        getSalesProcessById(processId);
        getStageById(stageId);

        try {
            salesProcessRepository.removeCurrentStageRelation(processId);
        } catch (Exception ignored) {
        }

        return salesProcessRepository.setCurrentStage(processId, stageId);
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
        Stage fromStage = getStageById(fromStageId);
        Stage toStage = getStageById(toStageId);

        boolean alreadyExists = fromStage.getNextStages().stream()
                .anyMatch(stage -> stage.getId().equals(toStageId));

        if (!alreadyExists) {
            fromStage.getNextStages().add(toStage);
        }

        return stageRepository.save(fromStage);
    }

    public Stage removeStageToStageRelation(String fromStageId, String toStageId) {
        Stage fromStage = getStageById(fromStageId);

        fromStage.getNextStages().removeIf(stage -> stage.getId().equals(toStageId));

        return stageRepository.save(fromStage);
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
}