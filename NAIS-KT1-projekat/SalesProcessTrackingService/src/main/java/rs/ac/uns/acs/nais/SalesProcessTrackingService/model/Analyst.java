package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Analyst")
public class Analyst {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;
    private String email;
    private String department;

    public Analyst() {
    }

    public Analyst(String fullName, String email, String department) {
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setDepartment(String department) { this.department = department; }
}
