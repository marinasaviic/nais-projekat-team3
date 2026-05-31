package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ElasticsearchDocumentService {
    public static final String DRUGS_INDEX = "drugs";
    public static final String REPORTS_INDEX = "adverse_event_reports";

    private final RestClient client;
    private final ObjectMapper objectMapper;

    public ElasticsearchDocumentService(RestClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void createIndexIfMissing(String index, Map<String, Object> mapping) throws IOException {
        Request exists = new Request("HEAD", "/" + index);
        try {
            client.performRequest(exists);
            return;
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() != 404) {
                throw ex;
            }
        }

        Request create = new Request("PUT", "/" + index);
        create.setEntity(jsonEntity(mapping));
        client.performRequest(create);
    }

    public void deleteIndex(String index) throws IOException {
        try {
            client.performRequest(new Request("DELETE", "/" + index));
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() != 404) {
                throw ex;
            }
        }
    }

    public void forceCreateIndex(String index, Map<String, Object> mapping) throws IOException {
        deleteIndex(index);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        Request create = new Request("PUT", "/" + index);
        create.setEntity(jsonEntity(mapping));
        client.performRequest(create);
    }

    public long count(String index) throws IOException {
        try {
            Response response = client.performRequest(new Request("GET", "/" + index + "/_count"));
            return objectMapper.readTree(response.getEntity().getContent()).path("count").asLong();
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() == 404) {
                return 0L;
            }
            throw ex;
        }
    }

    public JsonNode create(String index, String id, Object document) throws IOException {
        Request request = new Request("PUT", "/" + index + "/_doc/" + id + "?refresh=true");
        request.setEntity(jsonEntity(document));
        return execute(request);
    }

    public Optional<JsonNode> get(String index, String id) throws IOException {
        try {
            JsonNode response = execute(new Request("GET", "/" + index + "/_doc/" + id));
            return response.path("found").asBoolean() ? Optional.of(response.path("_source")) : Optional.empty();
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    public JsonNode update(String index, String id, Object document) throws IOException {
        Map<String, Object> body = Map.of("doc", document, "doc_as_upsert", true);
        Request request = new Request("POST", "/" + index + "/_update/" + id + "?refresh=true");
        request.setEntity(jsonEntity(body));
        return execute(request);
    }

    public void delete(String index, String id) throws IOException {
        Request request = new Request("DELETE", "/" + index + "/_doc/" + id + "?refresh=true");
        try {
            client.performRequest(request);
        } catch (ResponseException ex) {
            if (ex.getResponse().getStatusLine().getStatusCode() != 404) {
                throw ex;
            }
        }
    }

    public JsonNode search(String index, Map<String, Object> body) throws IOException {
        Request request = new Request("GET", "/" + index + "/_search");
        request.setEntity(jsonEntity(body));
        return execute(request);
    }

    public void bulk(String ndjson) throws IOException {
        Request request = new Request("POST", "/_bulk?refresh=true");
        request.setEntity(new NStringEntity(ndjson, ContentType.create("application/x-ndjson", "UTF-8")));
        client.performRequest(request);
    }

    public Map<String, Object> drugIndexMapping() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", keyword());
        properties.put("name", textWithKeyword());
        properties.put("activeSubstance", keyword());
        properties.put("manufacturer", keyword());
        properties.put("therapeuticClass", keyword());
        properties.put("prescriptionType", keyword());
        properties.put("description", Map.of("type", "text", "analyzer", "standard"));
        properties.put("commonSideEffects", keyword());
        properties.put("riskScore", Map.of("type", "double"));
        properties.put("reportedCases", Map.of("type", "integer"));
        return Map.of("mappings", Map.of("properties", properties));
    }

    public Map<String, Object> reportIndexMapping() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", keyword());
        properties.put("drugId", keyword());
        properties.put("drugName", textWithKeyword());
        properties.put("activeSubstance", keyword());
        properties.put("reactionType", keyword());
        properties.put("severity", keyword());
        properties.put("patientAgeGroup", keyword());
        properties.put("patientAge", Map.of("type", "integer"));
        properties.put("region", keyword());
        properties.put("reporterType", keyword());
        properties.put("description", Map.of("type", "text", "analyzer", "standard"));
        properties.put("eventDate", Map.of("type", "date"));
        properties.put("hospitalizationRequired", Map.of("type", "boolean"));
        properties.put("outcomeScore", Map.of("type", "double"));
        return Map.of("mappings", Map.of("properties", properties));
    }

    private Map<String, Object> keyword() {
        return Map.of("type", "keyword");
    }

    private Map<String, Object> textWithKeyword() {
        return Map.of("type", "text", "fields", Map.of("keyword", Map.of("type", "keyword")));
    }

    private JsonNode execute(Request request) throws IOException {
        Response response = client.performRequest(request);
        return objectMapper.readTree(response.getEntity().getContent());
    }

    private NStringEntity jsonEntity(Object body) throws IOException {
        return new NStringEntity(objectMapper.writeValueAsString(body), ContentType.APPLICATION_JSON);
    }
}
