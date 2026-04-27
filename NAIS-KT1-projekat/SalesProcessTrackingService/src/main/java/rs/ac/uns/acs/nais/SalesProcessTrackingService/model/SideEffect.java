package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SideEffect")
public class SideEffect {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private String severity;

    public SideEffect() {
    }

    public SideEffect(String name, String description, String severity) {
        this.name = name;
        this.description = description;
        this.severity = severity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}