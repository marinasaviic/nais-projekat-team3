package collab.report.service;

import collab.report.dto.CollaborativePricelistSummaryRow;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollaborativePricelistReportNeo4jReaderTest {

    @Test
    void mapsScalarReportRecordWithoutRootNode() {
        Record record = mock(Record.class);
        when(record.get("pricelistId")).thenReturn(Values.value("price-1"));
        when(record.get("name")).thenReturn(Values.value("Pricelist 1"));
        when(record.get("region")).thenReturn(Values.value("Region 4"));
        when(record.get("teamId")).thenReturn(Values.value("team-996"));
        when(record.get("teamName")).thenReturn(Values.value("Pricing Team 996"));
        when(record.get("currentStatus")).thenReturn(Values.value("ACTIVE"));
        when(record.get("creatorUserId")).thenReturn(Values.value("user-1"));
        when(record.get("numberOfCollaborators")).thenReturn(Values.value(2));

        CollaborativePricelistReportNeo4jReader reader = new CollaborativePricelistReportNeo4jReader(null);

        CollaborativePricelistSummaryRow row = reader.toSummaryRow(record);

        assertThat(row.getPricelistId()).isEqualTo("price-1");
        assertThat(row.getName()).isEqualTo("Pricelist 1");
        assertThat(row.getRegion()).isEqualTo("Region 4");
        assertThat(row.getTeamId()).isEqualTo("team-996");
        assertThat(row.getTeamName()).isEqualTo("Pricing Team 996");
        assertThat(row.getCurrentStatus()).isEqualTo("ACTIVE");
        assertThat(row.getCreatorUserId()).isEqualTo("user-1");
        assertThat(row.getNumberOfCollaborators()).isEqualTo(2);
    }

    @Test
    void mapsMissingOptionalScalarValuesToReportDefaults() {
        Record record = mock(Record.class);
        Value nullValue = nullValue();
        when(record.get("pricelistId")).thenReturn(Values.value("price-1"));
        when(record.get("name")).thenReturn(Values.value("Pricelist 1"));
        when(record.get("region")).thenReturn(nullValue);
        when(record.get("teamId")).thenReturn(nullValue);
        when(record.get("teamName")).thenReturn(nullValue);
        when(record.get("currentStatus")).thenReturn(nullValue);
        when(record.get("creatorUserId")).thenReturn(nullValue);
        when(record.get("numberOfCollaborators")).thenReturn(nullValue);

        CollaborativePricelistReportNeo4jReader reader = new CollaborativePricelistReportNeo4jReader(null);

        CollaborativePricelistSummaryRow row = reader.toSummaryRow(record);

        assertThat(row.getRegion()).isEqualTo("N/A");
        assertThat(row.getTeamId()).isEqualTo("N/A");
        assertThat(row.getTeamName()).isEqualTo("N/A");
        assertThat(row.getCurrentStatus()).isEqualTo("UNKNOWN");
        assertThat(row.getCreatorUserId()).isEqualTo("N/A");
        assertThat(row.getNumberOfCollaborators()).isZero();
    }

    private Value nullValue() {
        Value value = mock(Value.class);
        when(value.isNull()).thenReturn(true);
        return value;
    }
}
