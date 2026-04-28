package rs.ac.uns.acs.nais.ProductPortfolioService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("LifecycleStatus")
public class LifecycleStatus {

    @Id
    private String id;
    private String name;

    public LifecycleStatus() {
    }

    public LifecycleStatus(String id, String name) {
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
}