package rs.ac.uns.acs.nais.CollaborativePricelistService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Pricelist")
public class Pricelist {

    @Id
    private String id;
    private String name;
    private String status;
    private Integer version;

    public Pricelist() {
    }

    public Pricelist(String id, String name, String status, Integer version) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.version = version;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
