package rs.ac.uns.acs.nais.AdverseEffectsSearchService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.service.ElasticsearchDocumentService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/adverse-event-reports")
public class AdverseEventReportController {
    private final ElasticsearchDocumentService elasticsearch;

    public AdverseEventReportController(ElasticsearchDocumentService elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    @PostMapping
    public JsonNode create(@RequestBody AdverseEventReportDocument report) throws IOException {
        return elasticsearch.create(ElasticsearchDocumentService.REPORTS_INDEX, report.getId(), report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> get(@PathVariable String id) throws IOException {
        return elasticsearch.get(ElasticsearchDocumentService.REPORTS_INDEX, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public JsonNode update(@PathVariable String id, @RequestBody AdverseEventReportDocument report) throws IOException {
        report.setId(id);
        return elasticsearch.update(ElasticsearchDocumentService.REPORTS_INDEX, id, report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        elasticsearch.delete(ElasticsearchDocumentService.REPORTS_INDEX, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public JsonNode list(@RequestParam(defaultValue = "20") int size) throws IOException {
        return elasticsearch.search(ElasticsearchDocumentService.REPORTS_INDEX, Map.of("size", size, "query", Map.of("match_all", Map.of())));
    }
}
