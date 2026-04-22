package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("SalesRepresentative")
public class SalesRepresentative {

    @Id
    private String id;
    private String name;

    @Relationship(type = "MANAGES", direction = Relationship.Direction.OUTGOING)
    private List<SalesProcess> managedProcesses = new ArrayList<>();

    public SalesRepresentative() {
    }

    public SalesRepresentative(String id, String name) {
        this.id = id;
        this.name = name;
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

    public List<SalesProcess> getManagedProcesses() {
        return managedProcesses;
    }

    public void setManagedProcesses(List<SalesProcess> managedProcesses) {
        this.managedProcesses = managedProcesses;
    }
}