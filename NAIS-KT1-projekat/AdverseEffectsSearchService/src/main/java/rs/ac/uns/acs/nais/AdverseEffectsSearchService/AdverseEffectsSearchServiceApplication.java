package rs.ac.uns.acs.nais.AdverseEffectsSearchService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AdverseEffectsSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdverseEffectsSearchServiceApplication.class, args);
    }
}
