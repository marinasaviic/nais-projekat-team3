package searchanalytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SearchAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchAnalyticsServiceApplication.class, args);
    }
}
