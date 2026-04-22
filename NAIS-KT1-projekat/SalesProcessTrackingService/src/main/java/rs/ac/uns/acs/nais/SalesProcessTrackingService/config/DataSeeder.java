package rs.ac.uns.acs.nais.SalesProcessTrackingService.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Customer;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesProcess;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.SalesRepresentative;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.model.Stage;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.SalesProcessRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.SalesRepresentativeRepository;
import rs.ac.uns.acs.nais.SalesProcessTrackingService.repository.StageRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            CustomerRepository customerRepository,
            StageRepository stageRepository,
            SalesRepresentativeRepository salesRepresentativeRepository,
            SalesProcessRepository salesProcessRepository
    ) {
        return args -> {
            if (customerRepository.count() > 0 || stageRepository.count() > 0
                    || salesRepresentativeRepository.count() > 0 || salesProcessRepository.count() > 0) {
                return;
            }

            // STAGES
            Stage qualification = new Stage("stage-1", "Lead Qualification");
            Stage negotiation = new Stage("stage-2", "Negotiation");
            Stage offerSent = new Stage("stage-3", "Offer Sent");
            Stage closedWon = new Stage("stage-4", "Closed Won");
            Stage closedLost = new Stage("stage-5", "Closed Lost");

            qualification.getNextStages().add(negotiation);
            negotiation.getNextStages().add(offerSent);
            offerSent.getNextStages().add(closedWon);
            offerSent.getNextStages().add(closedLost);

            stageRepository.save(qualification);
            stageRepository.save(negotiation);
            stageRepository.save(offerSent);
            stageRepository.save(closedWon);
            stageRepository.save(closedLost);

            // CUSTOMERS
            Customer benu = new Customer("customer-1", "Apoteka Benu", "Novi Sad");
            Customer galen = new Customer("customer-2", "Galen Pharm", "Beograd");
            Customer medis = new Customer("customer-3", "Medis Plus", "Niš");

            customerRepository.save(benu);
            customerRepository.save(galen);
            customerRepository.save(medis);

            // SALES REPRESENTATIVES
            SalesRepresentative ana = new SalesRepresentative("rep-1", "Ana Jovanovic");
            SalesRepresentative marko = new SalesRepresentative("rep-2", "Marko Ilic");

            salesRepresentativeRepository.save(ana);
            salesRepresentativeRepository.save(marko);

            // SALES PROCESSES
            SalesProcess process1 = new SalesProcess("process-1", "Benu Spring Campaign", "ACTIVE");
            SalesProcess process2 = new SalesProcess("process-2", "Galen New Product Launch", "ACTIVE");
            SalesProcess process3 = new SalesProcess("process-3", "Medis Contract Renewal", "CLOSED_WON");
            SalesProcess process4 = new SalesProcess("process-4", "Benu OTC Expansion", "CLOSED_LOST");

            salesProcessRepository.save(process1);
            salesProcessRepository.save(process2);
            salesProcessRepository.save(process3);
            salesProcessRepository.save(process4);

            // RELATIONSHIPS
            salesProcessRepository.connectCustomerToProcess("customer-1", "process-1");
            salesProcessRepository.connectCustomerToProcess("customer-2", "process-2");
            salesProcessRepository.connectCustomerToProcess("customer-3", "process-3");
            salesProcessRepository.connectCustomerToProcess("customer-1", "process-4");

            salesProcessRepository.connectRepresentativeToProcess("rep-1", "process-1");
            salesProcessRepository.connectRepresentativeToProcess("rep-1", "process-3");
            salesProcessRepository.connectRepresentativeToProcess("rep-2", "process-2");
            salesProcessRepository.connectRepresentativeToProcess("rep-2", "process-4");

            salesProcessRepository.setCurrentStage("process-1", "stage-2");
            salesProcessRepository.setCurrentStage("process-2", "stage-3");
            salesProcessRepository.setCurrentStage("process-3", "stage-4");
            salesProcessRepository.setCurrentStage("process-4", "stage-5");
        };
    }
}