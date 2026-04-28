package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Medication")
public class Medication {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String dosage;
    private String usageMethod;

    public Medication() {
    }

    public Medication(String name, String dosage, String usageMethod) {
        this.name = name;
        this.dosage = dosage;
        this.usageMethod = usageMethod;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public String getUsageMethod() {
        return usageMethod;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setUsageMethod(String usageMethod) {
        this.usageMethod = usageMethod;
    }
}