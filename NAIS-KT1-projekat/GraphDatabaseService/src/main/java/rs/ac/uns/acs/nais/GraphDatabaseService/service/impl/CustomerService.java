package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.OrderDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.OrderItemDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ReviewDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Customer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Product;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Review;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ProductRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.ICustomerService;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for customer-related operations in the Neo4j graph.
 *
 * Handles CRUD on Customer nodes and graph relationship operations:
 *   - addReview: creates a REVIEWED relationship to a Product node
 *   - addPurchase: creates or updates PURCHASED relationships for each order item
 *   - recommendProductsByPurchaseHistory / recommendProductsByReviews: delegates to
 *     custom Cypher queries in the product repository
 */
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CustomerService(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /** Saves a new customer and marks them as active. */
    @Override
    public Customer addNewCustomer(Customer customer) {
        customer.setActive(true);
        return customerRepository.save(customer);
    }

    /**
     * Soft-deletes a customer by setting active=false.
     * Returns false if no customer with the given email exists.
     */
    @Override
    public boolean deleteCustomer(String customerEmail) {
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer != null) {
            customer.setActive(false);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    /**
     * Updates a customer's email address.
     * Returns false if no customer with the old email exists.
     */
    @Override
    public boolean updateCustomer(String customerEmailOld, String customerEmailNew) {
        Customer customer = customerRepository.findByEmail(customerEmailOld);
        if (customer != null) {
            customer.setEmail(customerEmailNew);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    /**
     * Attaches a REVIEWED relationship from the customer to the product,
     * storing the rating score on the relationship.
     * Returns null if the customer or product cannot be found.
     */
    @Override
    public Customer addReview(ReviewDTO reviewDTO) {
        Customer customer = customerRepository.findByEmail(reviewDTO.getCustomerEmail());
        Optional<Product> product = productRepository.findById(reviewDTO.getProductId());
        if (customer != null && product.isPresent()) {
            Review review = new Review();
            review.setProduct(product.get());
            review.setRating(reviewDTO.getRating());
            customer.addReview(review);
            return customerRepository.save(customer);
        }
        return null;
    }

    /**
     * Creates or increments PURCHASED relationships for each item in an order.
     * Uses hasPurchasedProduct to decide between a MERGE increment and a CREATE.
     */
    @Override
    public void addPurchase(OrderDTO orderDTO) {
        for (OrderItemDTO orderItemDTO : orderDTO.getItems()) {
            if (customerRepository.hasPurchasedProduct(orderDTO.getCustomerId(), orderItemDTO.getProductId())) {
                customerRepository.purchaseProduct(orderDTO.getCustomerId(), orderItemDTO.getProductId());
            } else {
                customerRepository.createPurchase(orderDTO.getCustomerId(), orderItemDTO.getProductId());
            }
        }
    }

    @Override
    public List<Product> recommendProductsByPurchaseHistory(Long customerId) {
        return productRepository.recommendProductsByPurchaseHistory(customerId);
    }

    @Override
    public List<Product> recommendProductsByReviews(Long customerId) {
        return productRepository.recommendProductsByReviews(customerId);
    }
}
