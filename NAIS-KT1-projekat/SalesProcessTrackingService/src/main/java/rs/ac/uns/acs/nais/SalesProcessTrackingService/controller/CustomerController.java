package rs.ac.uns.acs.nais.SalesProcessTrackingService.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Customer;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.service.GraphSalesService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final GraphSalesService graphSalesService;

    public CustomerController(GraphSalesService graphSalesService) {
        this.graphSalesService = graphSalesService;
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return graphSalesService.createCustomer(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return graphSalesService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable String id) {
        return graphSalesService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        return graphSalesService.updateCustomer(id, customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable String id) {
        graphSalesService.deleteCustomer(id);
    }

    @PostMapping("/{customerId}/processes/{processId}")
    public void connectCustomerToProcess(@PathVariable String customerId, @PathVariable String processId) {
        graphSalesService.connectCustomerToProcess(customerId, processId);
    }

    @DeleteMapping("/{customerId}/processes/{processId}")
    public void removeCustomerProcessRelation(@PathVariable String customerId, @PathVariable String processId) {
        graphSalesService.removeCustomerProcessRelation(customerId, processId);
    }
}