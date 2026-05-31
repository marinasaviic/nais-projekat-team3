package searchanalytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import searchanalytics.dto.SearchResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticsearchClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ElasticsearchClientService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void createIndexIfMissing(String index, Map<String, Object> mapping) throws IOException {
        try {
            Response existsResponse = restClient.performRequest(new Request("HEAD", "/" + index));
            if (existsResponse.getStatusLine().getStatusCode() == 200) return;
        } catch (ResponseException exception) {
            if (exception.getResponse().getStatusLine().getStatusCode() != 404) {
                throw exception;
            }
        }

        Request request = jsonRequest("PUT", "/" + index, mapping);
        restClient.performRequest(request);
    }

    public void deleteIndexIfExists(String index) throws IOException {
        Request request = new Request("DELETE", "/" + index);
        request.addParameter("ignore_unavailable", "true");
        restClient.performRequest(request);
    }

    public Map<String, Object> save(String index, String id, Object document) throws IOException {
        Request request = jsonRequest("PUT", "/" + index + "/_doc/" + id, document);
        Response response = restClient.performRequest(request);
        return readBody(response);
    }

    public Map<String, Object> getById(String index, String id) throws IOException {
        Request request = new Request("GET", "/" + index + "/_doc/" + id);
        Response response = restClient.performRequest(request);
        return readBody(response);
    }

    public void delete(String index, String id) throws IOException {
        restClient.performRequest(new Request("DELETE", "/" + index + "/_doc/" + id));
    }

    public SearchResponse search(String index, Map<String, Object> body) throws IOException {
        Request request = jsonRequest("GET", "/" + index + "/_search", body);
        Response response = restClient.performRequest(request);
        Map<String, Object> result = readBody(response);

        Map<String, Object> hitsObject = castMap(result.get("hits"));
        Map<String, Object> totalObject = castMap(hitsObject.get("total"));
        long total = ((Number) totalObject.get("value")).longValue();
        List<Map<String, Object>> hitRows = (List<Map<String, Object>>) hitsObject.getOrDefault("hits", List.of());
        List<Map<String, Object>> sources = hitRows.stream()
                .map(hit -> castMap(hit.get("_source")))
                .collect(Collectors.toList());
        Map<String, Object> aggregations = castMap(result.getOrDefault("aggregations", Map.of()));
        return new SearchResponse(total, sources, aggregations);
    }

    public long count(String index) throws IOException {
        Response response = restClient.performRequest(new Request("GET", "/" + index + "/_count"));
        Map<String, Object> body = readBody(response);
        return ((Number) body.get("count")).longValue();
    }

    private Request jsonRequest(String method, String endpoint, Object body) throws IOException {
        Request request = new Request(method, endpoint);
        String json = objectMapper.writeValueAsString(body);
        request.setEntity(new NStringEntity(json, ContentType.APPLICATION_JSON));
        return request;
    }

    private Map<String, Object> readBody(Response response) throws IOException {
        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) return Map.of();
        return objectMapper.readValue(body, new TypeReference<>() {});
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object object) {
        if (object == null) return Map.of();
        return (Map<String, Object>) object;
    }
}
