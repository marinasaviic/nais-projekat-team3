package rs.ac.uns.acs.nais.CollaborativePricelistService;

import collab.CollaborativePricelistServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = CollaborativePricelistServiceApplication.class,
        properties = {
                "app.seed-data=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
        })
class CollaborativePricelistServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
