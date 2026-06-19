package collab.report.dto;

public interface CollaborativePricelistSummaryProjection {

    String getPricelistId();

    String getName();

    String getRegion();

    String getTeamId();

    String getTeamName();

    String getCurrentStatus();

    String getCreatorUserId();

    Integer getNumberOfCollaborators();
}
