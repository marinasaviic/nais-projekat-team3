package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Customer;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Product;

import java.util.List;

/**
 * Neo4j repository for Customer nodes.
 *
 * In addition to standard Spring Data CRUD, declares custom Cypher queries for
 * managing the PURCHASED relationship (create, increment, check, delete) used by
 * both the choreography and orchestration Saga patterns.
 */
@Repository
public interface CustomerRepository extends Neo4jRepository<Customer, Long> {

    /** Finds a customer by their unique email address. Returns null if not found. */
    Customer findByEmail(String email);

    /**
     * Increments the purchase counter on an existing PURCHASED relationship, or creates
     * it with numberOfPurchasedItems=1 if no relationship exists yet (idempotent MERGE).
     *
     * @param customerId idOriginal of the customer node
     * @param productId  idOriginal of the product node
     */
    @Query("MATCH (c:Customer {idOriginal: $customerId}) " +
            "OPTIONAL MATCH (c)-[r:PURCHASED]->(p:Product {idOriginal: $productId}) " +
            "WITH c, p, COALESCE(r, 0) AS rel " +
            "MERGE (c)-[purchase: PURCHASED]->(p) " +
            "ON CREATE SET purchase.numberOfPurchasedItems = 1 " +
            "ON MATCH SET purchase.numberOfPurchasedItems = rel.numberOfPurchasedItems + 1")
    void purchaseProduct(Long customerId, String productId);

    /**
     * Returns true if a PURCHASED relationship already exists between the given customer
     * and product. Used to decide between purchaseProduct (increment) and createPurchase (new).
     *
     * @param customerId idOriginal of the customer node
     * @param productId  idOriginal of the product node
     */
    @Query("MATCH (c:Customer {idOriginal: $customerId})-[r:PURCHASED]->(p:Product {idOriginal: $productId}) " +
            "RETURN count(r) > 0")
    boolean hasPurchasedProduct(Long customerId, String productId);

    /**
     * Creates a new PURCHASED relationship with numberOfPurchasedItems=1.
     * Caller must verify the relationship does not already exist to avoid duplicates.
     *
     * @param customerId idOriginal of the customer node
     * @param productId  idOriginal of the product node
     */
    @Query("MATCH (c:Customer {idOriginal: $customerId}) " +
            "MATCH (p:Product {idOriginal: $productId}) " +
            "CREATE (c)-[purchase:PURCHASED]->(p) " +
            "SET purchase.numberOfPurchasedItems = 1")
    void createPurchase(Long customerId, String productId);

    /**
     * Deletes the PURCHASED relationship between a customer and a product.
     * Used as the compensating transaction in the Saga pattern when a downstream step fails.
     * Idempotent: if the relationship does not exist, the query is a no-op.
     *
     * @param customerId idOriginal of the customer node
     * @param productId  idOriginal of the product node
     */
    @Query("MATCH (c:Customer {idOriginal: $customerId})-[r:PURCHASED]->(p:Product {idOriginal: $productId}) " +
            "DELETE r")
    void deletePurchase(Long customerId, String productId);
}
