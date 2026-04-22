package rs.ac.uns.acs.nais.SalesProcessTrackingService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Stage")
public class Stage {

    @Id
    private String id;
    private String name;

    @Relationship(type = "ALLOWED_TO", direction = Relationship.Direction.OUTGOING)
    private List<Stage> nextStages = new ArrayList<>();

    public Stage() {
    }

    public Stage(String id, String name) {
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

    public List<Stage> getNextStages() {
        return nextStages;
    }

    public void setNextStages(List<Stage> nextStages) {
        this.nextStages = nextStages;
    }
}