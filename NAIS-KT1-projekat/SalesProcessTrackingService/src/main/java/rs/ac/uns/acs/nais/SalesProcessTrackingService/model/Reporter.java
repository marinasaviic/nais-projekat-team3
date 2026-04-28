package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Reporter")
public class Reporter {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;
    private String type;
    private Integer age;
    private String gender;

    public Reporter() {
    }

    public Reporter(String fullName, String type, Integer age, String gender) {
        this.fullName = fullName;
        this.type = type;
        this.age = age;
        this.gender = gender;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getType() { return type; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setType(String type) { this.type = type; }
    public void setAge(Integer age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
}