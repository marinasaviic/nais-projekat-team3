package collab.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Configuration
public class DataSeeder {

        private static final int USER_COUNT = 1000;
        private static final int TEAM_COUNT = 1000;
        private static final int PRICELIST_COUNT = 1000;
        private static final int REGION_COUNT = 1000;
        private static final int ACTIVITIES_PER_USER = 4;
        private static final List<String> USER_POSITIONS = List.of("OWNER", "ANALYST", "SALES_MANAGER", "CONTRIBUTOR");
        private static final List<String> TEAM_TYPES = List.of("REGIONAL", "SEGMENT", "CAMPAIGN");
        private static final List<String> PRICELIST_STATUSES = List.of("ACTIVE", "DRAFT", "ARCHIVED");
        private static final List<String> ACTION_TYPES = List.of("CREATE", "UPDATE", "PUBLISH", "DELETE", "ARCHIVE", "REVIEW", "APPROVE", "ACTIVATE");
        private static final List<String> COVERAGE_LEVELS = List.of("LOW", "MEDIUM", "HIGH");

    @Bean
    @ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
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

            List<TeamUser> teamUsers = new ArrayList<>(USER_COUNT);
            List<Team> teams = new ArrayList<>(TEAM_COUNT);
            List<Pricelist> pricelists = new ArrayList<>(PRICELIST_COUNT);
            List<Region> regions = new ArrayList<>(REGION_COUNT);

            for (int index = 1; index <= USER_COUNT; index++) {
                TeamUser teamUser = new TeamUser(
                        "user-" + index,
                        "Team User " + index,
                        "user" + index + "@company.com",
                        USER_POSITIONS.get((index - 1) % USER_POSITIONS.size())
                );
                teamUsers.add(teamUser);
            }

            for (int index = 1; index <= TEAM_COUNT; index++) {
                Team team = new Team(
                        "team-" + index,
                        "Pricing Team " + index,
                        TEAM_TYPES.get((index - 1) % TEAM_TYPES.size())
                );
                teams.add(team);
            }

            for (int index = 1; index <= PRICELIST_COUNT; index++) {
                Pricelist pricelist = new Pricelist(
                        "price-" + index,
                        "Pricelist " + index,
                        PRICELIST_STATUSES.get((index - 1) % PRICELIST_STATUSES.size()),
                        String.valueOf((index - 1) % 9 + 1)
                );
                pricelists.add(pricelist);
            }

            for (int index = 1; index <= REGION_COUNT; index++) {
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
                        Map<String, List<String>> userTeams = new LinkedHashMap<>();
                        Map<String, List<String>> teamPricelists = new LinkedHashMap<>();
                        Map<String, List<String>> pricelistRegions = new LinkedHashMap<>();

                        for (int index = 1; index <= USER_COUNT; index++) {
                                String userId = "user-" + index;
                                String primaryTeamId = "team-" + ((index - 1) % TEAM_COUNT + 1);
                                String secondaryTeamId = "team-" + ((index + 2) % TEAM_COUNT + 1);

                                userTeams.put(userId, List.of(primaryTeamId, secondaryTeamId));
                                collaborationRepository.addUserToTeam(userId, primaryTeamId, USER_POSITIONS.get((index - 1) % USER_POSITIONS.size()), baseTime.minusDays(index));
                                collaborationRepository.addUserToTeam(userId, secondaryTeamId, "CONTRIBUTOR", baseTime.minusDays(index / 2L));
                        }

                        for (int index = 1; index <= TEAM_COUNT; index++) {
                                String teamId = "team-" + index;
                                String primaryPricelistId = "price-" + ((index - 1) % PRICELIST_COUNT + 1);
                                String secondaryPricelistId = "price-" + ((index + 4) % PRICELIST_COUNT + 1);
                                String backupPricelistId = "price-" + ((index + 8) % PRICELIST_COUNT + 1);

                                teamPricelists.put(teamId, List.of(primaryPricelistId, secondaryPricelistId, backupPricelistId));
                                collaborationRepository.assignTeamToPricelist(teamId, primaryPricelistId, "PRIMARY", baseTime.minusDays(index));
                                collaborationRepository.assignTeamToPricelist(teamId, secondaryPricelistId, "SUPPORT", baseTime.minusDays(index + 1L));
                                collaborationRepository.assignTeamToPricelist(teamId, backupPricelistId, "REVIEW", baseTime.minusDays(index + 2L));
                        }

                        for (int index = 1; index <= PRICELIST_COUNT; index++) {
                                String pricelistId = "price-" + index;
                                String primaryRegionId = "region-" + ((index - 1) % REGION_COUNT + 1);
                                String secondaryRegionId = "region-" + ((index + 2) % REGION_COUNT + 1);

                                pricelistRegions.put(pricelistId, List.of(primaryRegionId, secondaryRegionId));
                                collaborationRepository.connectPricelistToRegion(pricelistId, primaryRegionId, COVERAGE_LEVELS.get((index - 1) % COVERAGE_LEVELS.size()));
                                collaborationRepository.connectPricelistToRegion(pricelistId, secondaryRegionId, COVERAGE_LEVELS.get(index % COVERAGE_LEVELS.size()));
                        }

                        int activityIndex = 1;
                        for (int index = 1; index <= USER_COUNT; index++) {
                                String userId = "user-" + index;
                                List<String> assignedTeams = userTeams.get(userId);

                                for (int activityRound = 0; activityRound < ACTIVITIES_PER_USER; activityRound++) {
                                        String teamId = assignedTeams.get(activityRound % assignedTeams.size());
                                        List<String> teamPricelistAssignments = teamPricelists.get(teamId);
                                        String pricelistId = teamPricelistAssignments.get(activityRound % teamPricelistAssignments.size());
                                        List<String> regionsForPricelist = pricelistRegions.get(pricelistId);
                                        String regionId = regionsForPricelist.get(activityRound % regionsForPricelist.size());
                                        String actionType = ACTION_TYPES.get((activityIndex - 1) % ACTION_TYPES.size());
                                        int durationMinutes = 10 + ((index + activityRound) % 45);
                                        ZonedDateTime timestamp = baseTime.minusHours(index * 2L + activityRound).minusMinutes(activityRound * 7L);

                                        collaborationRepository.logUserActionOnPricelist(
                                                        userId,
                                                        pricelistId,
                                                        actionType,
                                                        timestamp,
                                                        durationMinutes
                                        );

                                        activityLogRepository.save(new ActivityLog(
                                                        "activity-" + activityIndex,
                                                        actionType,
                                                        timestamp,
                                                        durationMinutes,
                                                        "" + userId + " " + actionType + " on " + pricelistId + " for " + regionId,
                                                        userId,
                                                        teamId,
                                                        pricelistId,
                                                        regionId
                                        ));

                                        activityIndex++;
                                }
            }
        };
    }
}
