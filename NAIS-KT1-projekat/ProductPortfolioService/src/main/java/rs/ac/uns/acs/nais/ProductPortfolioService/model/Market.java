package rs.ac.uns.acs.nais.ProductPortfolioService.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Market")
public class Market {

    @Id
    private String id;
    private String countryName;

    public Market() {
    }

    public Market(String id, String countryName) {
        this.id = id;
        this.countryName = countryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   public String getCountryName() {
        return countryName;
   }

   public void setCountryName(String countryName) {
        this.countryName = countryName;
   }
}