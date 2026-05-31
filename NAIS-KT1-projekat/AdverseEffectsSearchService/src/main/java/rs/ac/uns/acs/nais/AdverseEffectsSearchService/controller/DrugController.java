package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.DrugDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.ElasticsearchDocumentService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {
    private final ElasticsearchDocumentService elasticsearch;

    public DrugController(ElasticsearchDocumentService elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    @PostMapping
    public JsonNode create(@RequestBody DrugDocument drug) throws IOException {
        return elasticsearch.create(ElasticsearchDocumentService.DRUGS_INDEX, drug.getId(), drug);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> get(@PathVariable String id) throws IOException {
        return elasticsearch.get(ElasticsearchDocumentService.DRUGS_INDEX, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public JsonNode update(@PathVariable String id, @RequestBody DrugDocument drug) throws IOException {
        drug.setId(id);
        return elasticsearch.update(ElasticsearchDocumentService.DRUGS_INDEX, id, drug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        elasticsearch.delete(ElasticsearchDocumentService.DRUGS_INDEX, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public JsonNode list(@RequestParam(defaultValue = "20") int size) throws IOException {
        return elasticsearch.search(ElasticsearchDocumentService.DRUGS_INDEX, Map.of("size", size, "query", Map.of("match_all", Map.of())));
    }
}
