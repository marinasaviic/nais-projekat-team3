package rs.ac.uns.acs.nais.ProductPortfolioService.dto;

import java.util.List;

public record CategorySummaryDto(
        String id,
        String name,
        String level,
        String parentCategoryId,
        String parentCategoryName,
        List<String> subcategoryIds
) {
}