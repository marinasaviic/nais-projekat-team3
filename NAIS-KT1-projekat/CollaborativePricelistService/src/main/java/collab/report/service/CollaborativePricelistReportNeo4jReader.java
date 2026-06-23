package collab.report.service;

import collab.report.dto.CollaborativePricelistReportFilters;
import collab.report.dto.CollaborativePricelistSummaryRow;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CollaborativePricelistReportNeo4jReader {

    private static final String PRICELIST_REPORT_QUERY = """
            MATCH (p:Pricelist)
            OPTIONAL MATCH (t:Team)-[:WORKS_ON]->(p)
            OPTIONAL MATCH (p)-[:FOR_REGION]->(r:Region)
            OPTIONAL MATCH (u:TeamUser)-[:MEMBER_OF]->(t)
            WITH p, t, r, collect(DISTINCT u) AS collaborators
            OPTIONAL MATCH (creator:TeamUser)-[created:PERFORMED]->(p)
            WITH p, t, r, collaborators,
                 [creatorId IN collect(DISTINCT CASE
                    WHEN created.actionType IN ['CREATE', 'CREATED']
                    THEN creator.id
                    ELSE null
                 END) WHERE creatorId IS NOT NULL] AS creators
            WHERE ($pricelistId = '' OR p.id = $pricelistId)
              AND ($teamId = '' OR t.id = $teamId)
              AND ($region = '' OR r.id = $region OR r.name = $region)
              AND ($status = '' OR p.status = $status)
            RETURN p.id AS pricelistId,
                   p.name AS name,
                   coalesce(r.name, r.id, 'N/A') AS region,
                   coalesce(t.id, 'N/A') AS teamId,
                   coalesce(t.name, 'N/A') AS teamName,
                   coalesce(p.status, 'UNKNOWN') AS currentStatus,
                   coalesce(head(creators), 'N/A') AS creatorUserId,
                   size(collaborators) AS numberOfCollaborators
            ORDER BY p.id
            """;

    private final Neo4jClient neo4jClient;

    public CollaborativePricelistReportNeo4jReader(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<CollaborativePricelistSummaryRow> findPricelistReportRows(CollaborativePricelistReportFilters filters) {
        return neo4jClient.query(PRICELIST_REPORT_QUERY)
                .bindAll(Map.of(
                        "teamId", filterValue(filters.getTeamId()),
                        "region", filterValue(filters.getRegion()),
                        "status", filterValue(filters.getStatus()),
                        "pricelistId", filterValue(filters.getPricelistId())))
                .fetchAs(CollaborativePricelistSummaryRow.class)
                .mappedBy((typeSystem, record) -> toSummaryRow(record))
                .all()
                .stream()
                .toList();
    }

    CollaborativePricelistSummaryRow toSummaryRow(Record record) {
        CollaborativePricelistSummaryRow row = new CollaborativePricelistSummaryRow();
        row.setPricelistId(stringValue(record, "pricelistId", "N/A"));
        row.setName(stringValue(record, "name", "N/A"));
        row.setRegion(stringValue(record, "region", "N/A"));
        row.setTeamId(stringValue(record, "teamId", "N/A"));
        row.setTeamName(stringValue(record, "teamName", "N/A"));
        row.setCurrentStatus(stringValue(record, "currentStatus", "UNKNOWN"));
        row.setCreatorUserId(stringValue(record, "creatorUserId", "N/A"));
        row.setNumberOfCollaborators(integerValue(record, "numberOfCollaborators", 0));
        return row;
    }

    private String filterValue(String value) {
        return value == null ? "" : value;
    }

    private String stringValue(Record record, String key, String fallback) {
        Value value = record.get(key);
        if (value == null || value.isNull()) {
            return fallback;
        }
        return value.asString(fallback);
    }

    private Integer integerValue(Record record, String key, Integer fallback) {
        Value value = record.get(key);
        if (value == null || value.isNull()) {
            return fallback;
        }
        return value.asInt(fallback);
    }
}
