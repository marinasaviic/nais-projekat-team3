package collab.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.Neo4jClient;
import collab.model.ActivityLog;
import collab.model.Pricelist;
import collab.model.Region;
import collab.model.Team;
import collab.model.TeamUser;
import collab.repository.ActivityLogRepository;
import collab.repository.CollaborationRepository;
import collab.repository.PricelistRepository;
import collab.repository.RegionRepository;
import collab.repository.TeamRepository;
import collab.repository.TeamUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
public class DataSeeder {

        private static final int ENTITY_COUNT = 1000;
        private static final List<String> USER_POSITIONS = List.of("OWNER", "ANALYST", "SALES_MANAGER", "CONTRIBUTOR");
        private static final List<String> TEAM_TYPES = List.of("REGIONAL", "SEGMENT", "CAMPAIGN");
        private static final List<String> PRICELIST_STATUSES = List.of("ACTIVE", "DRAFT", "ARCHIVED");
        private static final List<String> ACTION_TYPES = List.of("CREATE", "UPDATE", "PUBLISH", "DELETE", "ARCHIVE", "REVIEW", "APPROVE", "ACTIVATE");
        private static final List<String> COVERAGE_LEVELS = List.of("LOW", "MEDIUM", "HIGH");

    @Bean
    CommandLineRunner seedData(
            Neo4jClient neo4jClient,
            TeamUserRepository teamUserRepository,
            TeamRepository teamRepository,
            PricelistRepository pricelistRepository,
            RegionRepository regionRepository,
            ActivityLogRepository activityLogRepository,
            CollaborationRepository collaborationRepository
    ) {
        return args -> {
                        neo4jClient.query("MATCH (n) DETACH DELETE n").run();

            List<TeamUser> teamUsers = new ArrayList<>(ENTITY_COUNT);
            List<Team> teams = new ArrayList<>(ENTITY_COUNT);
            List<Pricelist> pricelists = new ArrayList<>(ENTITY_COUNT);
            List<Region> regions = new ArrayList<>(ENTITY_COUNT);

            for (int index = 1; index <= ENTITY_COUNT; index++) {
                TeamUser teamUser = new TeamUser(
                        "user-" + index,
                        "Team User " + index,
                        "user" + index + "@company.com",
                        USER_POSITIONS.get((index - 1) % USER_POSITIONS.size())
                );
                teamUsers.add(teamUser);

                Team team = new Team(
                        "team-" + index,
                        "Pricing Team " + index,
                        TEAM_TYPES.get((index - 1) % TEAM_TYPES.size())
                );
                teams.add(team);

                Pricelist pricelist = new Pricelist(
                        "price-" + index,
                        "Pricelist " + index,
                        PRICELIST_STATUSES.get((index - 1) % PRICELIST_STATUSES.size()),
                        String.valueOf((index - 1) % 9 + 1)
                );
                pricelists.add(pricelist);

                Region region = new Region(
                        "region-" + index,
                        "Region " + index,
                        index % 2 == 0 ? "Serbia" : "Balkan"
                );
                regions.add(region);
            }

            teamUserRepository.saveAll(teamUsers);
            teamRepository.saveAll(teams);
            pricelistRepository.saveAll(pricelists);
            regionRepository.saveAll(regions);

            ZonedDateTime baseTime = ZonedDateTime.now(ZoneOffset.UTC);

            for (int index = 1; index <= ENTITY_COUNT; index++) {
                String userId = "user-" + index;
                String teamId = "team-" + index;
                String pricelistId = "price-" + index;
                String regionId = "region-" + index;

                collaborationRepository.addUserToTeam(userId, teamId, USER_POSITIONS.get((index - 1) % USER_POSITIONS.size()), baseTime.minusDays(index));
                collaborationRepository.assignTeamToPricelist(teamId, pricelistId, index % 2 == 0 ? "PRIMARY" : "SECONDARY", baseTime.minusDays(index / 2L));
                collaborationRepository.connectPricelistToRegion(pricelistId, regionId, COVERAGE_LEVELS.get((index - 1) % COVERAGE_LEVELS.size()));

                collaborationRepository.logUserActionOnPricelist(
                        userId,
                        pricelistId,
                        ACTION_TYPES.get((index - 1) % ACTION_TYPES.size()),
                        baseTime.minusMinutes(index),
                        (index % 60) + 5
                );

                activityLogRepository.save(new ActivityLog(
                        "activity-" + index,
                        ACTION_TYPES.get((index - 1) % ACTION_TYPES.size()),
                        baseTime.minusMinutes(index),
                        (index % 60) + 5,
                        "Seeded activity " + index,
                        userId,
                        teamId,
                        pricelistId,
                        regionId
                ));
            }
        };
    }
}
