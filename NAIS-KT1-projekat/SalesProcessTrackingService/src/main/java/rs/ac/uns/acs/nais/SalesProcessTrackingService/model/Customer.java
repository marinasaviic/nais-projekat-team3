package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Customer")
public class Customer {

    @Id
    private String id;
    private String name;
    private String city;

    @Relationship(type = "HAS_PROCESS", direction = Relationship.Direction.OUTGOING)
    private List<SalesProcess> processes = new ArrayList<>();

    public Customer() {
    }

    public Customer(String id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<SalesProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<SalesProcess> processes) {
        this.processes = processes;
    }
}