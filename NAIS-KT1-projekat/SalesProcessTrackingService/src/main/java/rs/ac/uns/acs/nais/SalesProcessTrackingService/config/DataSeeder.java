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

import java.util.ArrayList;
import java.util.List;

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

            // =====================
            // STAGES
            // =====================
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

            // =====================
            // CUSTOMERS
            // =====================
            List<Customer> customers = new ArrayList<>();
            String[] cities = {"Novi Sad", "Beograd", "Niš", "Kragujevac", "Subotica"};

            for (int i = 1; i <= 50; i++) {
                Customer customer = new Customer(
                        "customer-" + i,
                        "Pharmacy Customer " + i,
                        cities[i % cities.length]
                );
                customers.add(customerRepository.save(customer));
            }

            // =====================
            // SALES REPRESENTATIVES
            // =====================
            List<SalesRepresentative> representatives = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                SalesRepresentative representative = new SalesRepresentative(
                        "rep-" + i,
                        "Sales Representative " + i
                );
                representatives.add(salesRepresentativeRepository.save(representative));
            }

            // =====================
            // SALES PROCESSES
            // =====================
            String[] statuses = {"ACTIVE", "CLOSED_WON", "CLOSED_LOST"};
            Stage[] stages = {qualification, negotiation, offerSent, closedWon, closedLost};

            for (int i = 1; i <= 500; i++) {
                SalesProcess process = new SalesProcess(
                        "process-" + i,
                        "Sales Process " + i,
                        statuses[i % statuses.length]
                );

                salesProcessRepository.save(process);

                Customer customer = customers.get(i % customers.size());
                SalesRepresentative representative = representatives.get(i % representatives.size());
                Stage currentStage = stages[i % stages.length];

                salesProcessRepository.connectCustomerToProcess(customer.getId(), process.getId());
                salesProcessRepository.connectRepresentativeToProcess(representative.getId(), process.getId());
                salesProcessRepository.setCurrentStage(process.getId(), currentStage.getId());
            }
        };
    }
}