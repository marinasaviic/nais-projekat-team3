package timeseries.messaging;

import collab.saga.dto.LifecycleEventWrittenReply;
import collab.saga.dto.WriteLifecycleEventCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import timeseries.configuration.RabbitMQConfig;
import timeseries.model.PriceListLifecycleEvent;
import timeseries.service.PriceListLifecycleService;

import java.time.Instant;

@Component
public class PricelistLifecycleCommandListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricelistLifecycleCommandListener.class);

    private final PriceListLifecycleService priceListLifecycleService;

    public PricelistLifecycleCommandListener(PriceListLifecycleService priceListLifecycleService) {
        this.priceListLifecycleService = priceListLifecycleService;
    }

    @RabbitListener(queues = RabbitMQConfig.WRITE_LIFECYCLE_EVENT_QUEUE)
    public LifecycleEventWrittenReply handleWriteLifecycleEvent(WriteLifecycleEventCommand command) {
        try {
            LOGGER.info("Saga {} received lifecycle event write command for pricelist {}",
                    command.getSagaId(), command.getPricelistId());
            PriceListLifecycleEvent event = toEvent(command);
            boolean saved = priceListLifecycleService.save(event);
            LOGGER.info("Saga {} Influx lifecycle event write completed with success={}",
                    command.getSagaId(), saved);
            return new LifecycleEventWrittenReply(
                    command.getSagaId(),
                    saved,
                    saved ? null : "InfluxDB write returned false");
        } catch (Exception ex) {
            LOGGER.error("Saga {} Influx lifecycle event write failed", command.getSagaId(), ex);
            return new LifecycleEventWrittenReply(command.getSagaId(), false, ex.getMessage());
        }
    }

    private PriceListLifecycleEvent toEvent(WriteLifecycleEventCommand command) {
        PriceListLifecycleEvent event = new PriceListLifecycleEvent();
        event.setSagaId(command.getSagaId());
        event.setPricelistId(command.getPricelistId());
        event.setPriceListId(command.getPricelistId());
        event.setUserId(command.getUserId());
        event.setTeamId(command.getTeamId());
        event.setRegion(command.getRegion());
        event.setOperationType(command.getOperationType());
        event.setStatusFrom(command.getStatusFrom());
        event.setStatusTo(command.getStatusTo());
        event.setDurationMs(command.getDurationMs() == null ? null : command.getDurationMs().doubleValue());
        event.setSuccess(command.getSuccess());
        event.setErrorMessage(command.getErrorMessage());
        event.setTimestamp(command.getTimestamp() == null ? Instant.now() : command.getTimestamp());
        return event;
    }
}
