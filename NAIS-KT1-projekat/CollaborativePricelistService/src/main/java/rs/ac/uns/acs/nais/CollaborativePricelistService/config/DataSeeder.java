package rs.ac.uns.acs.nais.CollaborativePricelistService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.ActivityLog;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Pricelist;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Region;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.Team;
import rs.ac.uns.acs.nais.CollaborativePricelistService.model.TeamUser;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.ActivityLogRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.CollaborationRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.PricelistRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.RegionRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.TeamRepository;
import rs.ac.uns.acs.nais.CollaborativePricelistService.repository.TeamUserRepository;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            TeamUserRepository teamUserRepository,
            TeamRepository teamRepository,
            PricelistRepository pricelistRepository,
            RegionRepository regionRepository,
            ActivityLogRepository activityLogRepository,
            CollaborationRepository collaborationRepository
    ) {
        return args -> {
            if (teamUserRepository.count() > 0 || teamRepository.count() > 0 || pricelistRepository.count() > 0
                    || regionRepository.count() > 0 || activityLogRepository.count() > 0) {
                return;
            }

            TeamUser user1 = new TeamUser("user-1", "Ana Markovic", "ana@company.com", "OWNER");
            TeamUser user2 = new TeamUser("user-2", "Marko Ilic", "marko@company.com", "ANALYST");
            TeamUser user3 = new TeamUser("user-3", "Jelena Kovac", "jelena@company.com", "SALES_MANAGER");
            TeamUser user4 = new TeamUser("user-4", "Nikola Petrov", "nikola@company.com", "OWNER");

            teamUserRepository.save(user1);
            teamUserRepository.save(user2);
            teamUserRepository.save(user3);
            teamUserRepository.save(user4);

            Team teamA = new Team("team-1", "North Pricing Team", "REGIONAL");
            Team teamB = new Team("team-2", "Enterprise Pricing Team", "SEGMENT");
            Team teamC = new Team("team-3", "Promo Optimization Team", "CAMPAIGN");

            teamRepository.save(teamA);
            teamRepository.save(teamB);
            teamRepository.save(teamC);

            Pricelist p1 = new Pricelist("price-1", "Q2 Pharma Pricelist", "ACTIVE", 3);
            Pricelist p2 = new Pricelist("price-2", "Retail Spring Campaign", "ACTIVE", 5);
            Pricelist p3 = new Pricelist("price-3", "Wholesale Special Offer", "DRAFT", 2);
            Pricelist p4 = new Pricelist("price-4", "Regional OTC Updates", "ACTIVE", 4);

            pricelistRepository.save(p1);
            pricelistRepository.save(p2);
            pricelistRepository.save(p3);
            pricelistRepository.save(p4);

            Region r1 = new Region("region-1", "Vojvodina", "Serbia");
            Region r2 = new Region("region-2", "Belgrade", "Serbia");
            Region r3 = new Region("region-3", "Central Serbia", "Serbia");

            regionRepository.save(r1);
            regionRepository.save(r2);
            regionRepository.save(r3);

            collaborationRepository.addUserToTeam("user-1", "team-1", "OWNER", LocalDateTime.now().minusDays(20));
            collaborationRepository.addUserToTeam("user-2", "team-1", "CONTRIBUTOR", LocalDateTime.now().minusDays(18));
            collaborationRepository.addUserToTeam("user-2", "team-2", "ANALYST", LocalDateTime.now().minusDays(15));
            collaborationRepository.addUserToTeam("user-3", "team-2", "OWNER", LocalDateTime.now().minusDays(10));
            collaborationRepository.addUserToTeam("user-4", "team-3", "OWNER", LocalDateTime.now().minusDays(8));

            collaborationRepository.assignTeamToPricelist("team-1", "price-1", "PRIMARY", LocalDateTime.now().minusDays(12));
            collaborationRepository.assignTeamToPricelist("team-1", "price-4", "SECONDARY", LocalDateTime.now().minusDays(7));
            collaborationRepository.assignTeamToPricelist("team-2", "price-2", "PRIMARY", LocalDateTime.now().minusDays(9));
            collaborationRepository.assignTeamToPricelist("team-2", "price-3", "SECONDARY", LocalDateTime.now().minusDays(6));

            collaborationRepository.connectPricelistToRegion("price-1", "region-1", "HIGH");
            collaborationRepository.connectPricelistToRegion("price-2", "region-2", "HIGH");
            collaborationRepository.connectPricelistToRegion("price-3", "region-3", "MEDIUM");
            collaborationRepository.connectPricelistToRegion("price-4", "region-1", "LOW");

            collaborationRepository.logUserActionOnPricelist("user-1", "price-1", "CREATE", LocalDateTime.now().minusDays(11), 45);
            collaborationRepository.logUserActionOnPricelist("user-2", "price-1", "UPDATE", LocalDateTime.now().minusDays(6), 30);
            collaborationRepository.logUserActionOnPricelist("user-2", "price-2", "UPDATE", LocalDateTime.now().minusDays(4), 50);
            collaborationRepository.logUserActionOnPricelist("user-3", "price-2", "CREATE", LocalDateTime.now().minusDays(9), 60);
            collaborationRepository.logUserActionOnPricelist("user-4", "price-4", "UPDATE", LocalDateTime.now().minusDays(2), 35);
            collaborationRepository.logUserActionOnPricelist("user-1", "price-4", "UPDATE", LocalDateTime.now().minusHours(20), 25);

            activityLogRepository.save(new ActivityLog(
                    "activity-1", "CREATE", LocalDateTime.now().minusDays(11), 45,
                    "Initial creation of Q2 pricelist", "user-1", "team-1", "price-1", "region-1"
            ));
            
            // ============================================================================
            // ENHANCED ACTIVITY LOGS - FZ 2.4.1 Praćenje aktivnosti timova
            // ============================================================================
            // Raznovrsne aktivnosti sa različitim tipima, vremenima i trajanjima
            
            activityLogRepository.save(new ActivityLog(
                    "activity-2", "UPDATE", LocalDateTime.now().minusDays(6), 30,
                    "Updated pricing strategy for Q2", "user-2", "team-1", "price-1", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-3", "PUBLISH", LocalDateTime.now().minusDays(5), 15,
                    "Published Q2 pricelist to production", "user-1", "team-1", "price-1", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-4", "CREATE", LocalDateTime.now().minusDays(9), 60,
                    "Created retail spring campaign pricelist", "user-3", "team-2", "price-2", "region-2"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-5", "UPDATE", LocalDateTime.now().minusDays(4), 50,
                    "Changed discount coefficients for retail", "user-2", "team-2", "price-2", "region-2"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-6", "DELETE", LocalDateTime.now().minusDays(3), 20,
                    "Removed obsolete price tiers", "user-3", "team-2", "price-2", "region-2"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-7", "ARCHIVE", LocalDateTime.now().minusDays(2), 10,
                    "Archived previous version of pricelist", "user-4", "team-3", "price-4", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-8", "UPDATE", LocalDateTime.now().minusHours(20), 25,
                    "Regional correction for OTC segment", "user-1", "team-1", "price-4", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-9", "ACTIVATE", LocalDateTime.now().minusHours(18), 5,
                    "Activated Q2 pharma pricelist in system", "user-2", "team-1", "price-1", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-10", "REVIEW", LocalDateTime.now().minusHours(12), 40,
                    "Reviewed and analyzed competitive pricing", "user-3", "team-2", "price-2", "region-2"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-11", "UPDATE", LocalDateTime.now().minusHours(8), 35,
                    "Fine-tuned margin coefficients based on analysis", "user-1", "team-1", "price-1", "region-1"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-12", "APPROVE", LocalDateTime.now().minusHours(4), 15,
                    "Approved all pending price changes", "user-4", "team-3", "price-4", "region-1"
            ));
            
            // Dodatne aktivnosti za boljí analytics
            activityLogRepository.save(new ActivityLog(
                    "activity-13", "CREATE", LocalDateTime.now().minusDays(7), 55,
                    "Created new wholesale special offer", "user-2", "team-2", "price-3", "region-3"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-14", "UPDATE", LocalDateTime.now().minusDays(5), 45,
                    "Updated promotion conditions", "user-3", "team-2", "price-3", "region-3"
            ));
            
            activityLogRepository.save(new ActivityLog(
                    "activity-15", "DELETE", LocalDateTime.now().minusDays(1), 20,
                    "Removed expired promotional prices", "user-1", "team-1", "price-4", "region-1"
            ));
        };
    }
}
