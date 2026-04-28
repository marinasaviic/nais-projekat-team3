package rs.ac.uns.acs.nais.ProductPortfolioService.dto;

public record ProductSummaryDto(
        String id,
        String name,
        String code,
        String categoryId,
        String categoryName,
        String lifecycleStatusId,
        String lifecycleStatusName
) {
}