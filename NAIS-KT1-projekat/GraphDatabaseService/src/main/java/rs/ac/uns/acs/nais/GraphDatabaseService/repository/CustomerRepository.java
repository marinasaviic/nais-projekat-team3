package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends Neo4jRepository<Customer, String> {
    Optional<Customer> findByName(String name);
}