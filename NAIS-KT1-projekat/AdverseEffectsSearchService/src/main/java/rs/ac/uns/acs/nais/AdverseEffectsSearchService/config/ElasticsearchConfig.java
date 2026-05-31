package rs.ac.uns.acs.nais.AdverseEffectsSearchService.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClient elasticsearchRestClient(
            @Value("${elasticsearch.host}") String host,
            @Value("${elasticsearch.port}") int port,
            @Value("${elasticsearch.scheme}") String scheme
    ) {
        return RestClient.builder(new HttpHost(host, port, scheme)).build();
    }
}
