package rs.ac.uns.acs.nais.AdverseEffectsSearchService.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportProjectionDto;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.dto.AdverseReportSagaResponse;
import rs.ac.uns.acs.nais.AdverseEffectsSearchService.model.AdverseEventReportDocument;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdverseReportSagaService {
    private final ElasticsearchDocumentService elasticsearch;
    private final AdverseReportRedisProjectionService redisProjection;
    private final RedisKeyValueService redis;

    public AdverseReportSagaService(ElasticsearchDocumentService elasticsearch,
                                    AdverseReportRedisProjectionService redisProjection,
                                    RedisKeyValueService redis) {
        this.elasticsearch = elasticsearch;
        this.redisProjection = redisProjection;
        this.redis = redis;
    }

    public AdverseReportSagaResponse createTransactional(AdverseEventReportDocument report,
                                                         boolean simulateRedisFailure) throws IOException {
        validate(report);

        String sagaId = UUID.randomUUID().toString();
        List<String> completedSteps = new ArrayList<>();
        redisProjection.recordSagaState(sagaId, "STARTED", "Creating adverse event report " + report.getId());

        try {
            elasticsearch.create(ElasticsearchDocumentService.REPORTS_INDEX, report.getId(), report);
            completedSteps.add("ELASTICSEARCH_REPORT_WRITE");
            redisProjection.recordSagaState(sagaId, "ELASTICSEARCH_WRITTEN", "Report stored in Elasticsearch");

            if (simulateRedisFailure) {
                throw new IllegalStateException("Simulated Redis projection failure");
            }

            redisProjection.saveProjection(sagaId, report);
            completedSteps.add("REDIS_PROJECTION_WRITE");
            redisProjection.recordSagaState(sagaId, "COMPLETED", "Report stored in Elasticsearch and projected to Redis");

            return new AdverseReportSagaResponse(
                    sagaId,
                    "COMPLETED",
                    "Adverse event report stored in Elasticsearch and Redis.",
                    report,
                    completedSteps,
                    Instant.now()
            );
        } catch (RuntimeException | IOException ex) {
            boolean compensated = compensateElasticsearch(report.getId());
            redisProjection.recordSagaState(
                    sagaId,
                    compensated ? "COMPENSATED" : "COMPENSATION_FAILED",
                    "Redis step failed after Elasticsearch write. Elasticsearch compensation executed: " + compensated
            );
            throw new IllegalStateException("Transactional adverse report creation failed. Compensation executed: " + compensated, ex);
        }
    }

    public List<AdverseReportProjectionDto> latestRedisProjections(int limit) {
        return redisProjection.latestProjections(limit);
    }

    public Map<String, Long> severityCounts() {
        return redisProjection.severityCounts();
    }

    public List<Map<String, String>> latestSagaStates(int limit) {
        return redisProjection.sagaStates(limit);
    }

    private boolean compensateElasticsearch(String reportId) {
        try {
            elasticsearch.delete(ElasticsearchDocumentService.REPORTS_INDEX, reportId);
            redis.delete("report:" + reportId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void validate(AdverseEventReportDocument report) {
        if (report == null) {
            throw new IllegalArgumentException("Report body is required.");
        }
        if (isBlank(report.getId())) {
            throw new IllegalArgumentException("id is required.");
        }
        if (isBlank(report.getDrugId())) {
            throw new IllegalArgumentException("drugId is required.");
        }
        if (isBlank(report.getDrugName())) {
            throw new IllegalArgumentException("drugName is required.");
        }
        if (isBlank(report.getReactionType())) {
            throw new IllegalArgumentException("reactionType is required.");
        }
        if (isBlank(report.getSeverity())) {
            throw new IllegalArgumentException("severity is required.");
        }
        if (isBlank(report.getRegion())) {
            throw new IllegalArgumentException("region is required.");
        }
        if (report.getEventDate() == null) {
            report.setEventDate(LocalDate.now());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
