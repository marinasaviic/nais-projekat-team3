package rs.ac.uns.acs.nais.ProductPortfolioService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("ProductVariant")
public class ProductVariant {

    @Id
    private String id;
    private String name;
    private String strength;
    private String packageSize;
    private String form;

    @Relationship(type = "AVAILABLE_IN", direction = Relationship.Direction.OUTGOING)
    private List<Market> markets = new ArrayList<>();

    public ProductVariant() {
    }

    public ProductVariant(String id, String name, String strength, String packageSize, String form) {
        this.id = id;
        this.name = name;
        this.strength = strength;
        this.packageSize = packageSize;
        this.form = form;
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

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }
}