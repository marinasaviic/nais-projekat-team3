package rs.ac.uns.acs.nais.ProductPortfolioService.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Category")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Category {

    @Id
    private String id;
    private String name;
    private String level;

    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private Category parentCategory;

    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private List<Category> subcategories = new ArrayList<>();

    public Category() {
    }

    public Category(String id, String name, String level) {
        this.id = id;
        this.name = name;
        this.level = level;
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

   public String getLevel() {
        return level;
   }

   public void setLevel(String level) {
        this.level = level;
   }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }
}