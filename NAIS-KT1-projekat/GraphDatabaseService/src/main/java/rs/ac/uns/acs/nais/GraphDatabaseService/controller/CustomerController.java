package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import org.neo4j.cypherdsl.core.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.OrderDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReviewDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Customer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Product;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.impl.CustomerService;

import java.util.List;

/**
 * REST controller for customer management and customer-related graph operations.
 *
 * Base path: /customers.json
 *
 * Exposes CRUD operations for Customer nodes in Neo4j, plus endpoints to attach
 * reviews and purchases (PURCHASED / REVIEWED relationships) to customer nodes.
 */
@RestController
@RequestMapping("/customers.json")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** Returns all customer nodes from Neo4j. */
    @GetMapping("")
    public ResponseEntity<List<Customer>> findAll() {
        List<Customer> customers = customerService.findAll();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    /** Creates a new Customer node. Sets active=true by default. */
    @PostMapping
    public ResponseEntity<Customer> addNewCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.addNewCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * Soft-deletes a customer by setting active=false.
     * Returns 404 if no customer with the given email is found.
     */
    @DeleteMapping
    public ResponseEntity<Customer> deleteCustomer(@RequestParam String customerEmail) {
        if (customerService.deleteCustomer(customerEmail)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates the email address of an existing customer.
     * Returns 404 if no customer with the old email is found.
     */
    @PutMapping
    public ResponseEntity<Customer> updateCustomer(
            @RequestParam("customerEmailOld") String customerEmailOld,
            @RequestParam("customerEmailNew") String customerEmailNew) {
        if (customerService.updateCustomer(customerEmailOld, customerEmailNew)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Adds a REVIEWED relationship between a customer and a product.
     * Returns 404 if the customer or product does not exist.
     */
    @PostMapping("addReview")
    public ResponseEntity addNewReview(@RequestBody ReviewDTO reviewDTO) {
        if (customerService.addReview(reviewDTO) != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Adds PURCHASED relationships for all items in an order.
     * Delegates to CustomerService which handles idempotent MERGE logic per product.
     */
    @PostMapping("addPurchase")
    public ResponseEntity<Customer> addNewPurchase(@RequestBody OrderDTO orderDTO) {
        customerService.addPurchase(orderDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
