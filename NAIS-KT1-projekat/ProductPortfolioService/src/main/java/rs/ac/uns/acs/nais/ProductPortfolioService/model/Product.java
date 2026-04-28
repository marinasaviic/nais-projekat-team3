package rs.ac.uns.acs.nais.ProductPortfolioService.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Product")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product {

    @Id
    private String id;
    private String name;
    private String code;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private Category category;

    @Relationship(type = "HAS_VARIANT", direction = Relationship.Direction.OUTGOING)
    private List<ProductVariant> variants = new ArrayList<>();

    @Relationship(type = "HAS_STATUS", direction = Relationship.Direction.OUTGOING)
    private LifecycleStatus lifecycleStatus;

    public Product() {
    }

    public Product(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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

   public String getCode() {
        return code;
   }

   public void setCode(String code) {
        this.code = code;
   }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public LifecycleStatus getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }
}