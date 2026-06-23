package rs.ac.uns.acs.nais.TimeseriesDatabaseService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import timeseries.TimeseriesDatabaseServiceApplication;

@SpringBootTest(
        classes = TimeseriesDatabaseServiceApplication.class,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.rabbitmq.listener.simple.auto-startup=false"
        })
class TimeseriesDatabaseServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
