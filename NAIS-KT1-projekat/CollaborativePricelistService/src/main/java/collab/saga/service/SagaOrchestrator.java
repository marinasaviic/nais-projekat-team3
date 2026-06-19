package collab.saga.service;

import collab.config.RabbitMQConfig;
import collab.model.Pricelist;
import collab.saga.dto.ChangePricelistStatusCommand;
import collab.saga.dto.CreatePricelistCommand;
import collab.saga.dto.DeletePricelistCommand;
import collab.saga.dto.LifecycleEventWrittenReply;
import collab.saga.dto.PricelistOperationReply;
import collab.saga.dto.WriteLifecycleEventCommand;
import collab.saga.model.SagaInstance;
import collab.saga.model.SagaState;
import collab.service.CollaborationGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SagaOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaOrchestrator.class);

    private final CollaborationGraphService collaborationGraphService;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, SagaInstance> sagaStore = new ConcurrentHashMap<>();

    public SagaOrchestrator(CollaborationGraphService collaborationGraphService, RabbitTemplate rabbitTemplate) {
        this.collaborationGraphService = collaborationGraphService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public PricelistOperationReply startCreatePricelistSaga(CreatePricelistCommand command) {
        Instant startedAt = Instant.now();
        String sagaId = UUID.randomUUID().toString();
        if (command == null || command.getPricelist() == null) {
            SagaInstance saga = createSaga(sagaId, null, null, null,
                    "CREATED", null, null, null, startedAt);
            transition(saga, SagaState.FAILED, "Saga failed because pricelist payload is missing");
            return toReply(saga, false, "Pricelist payload is required");
        }
        Pricelist pricelist = command.getPricelist();
        if (pricelist.getId() == null || pricelist.getId().isBlank()) {
            pricelist.setId(UUID.randomUUID().toString());
        }

        SagaInstance saga = createSaga(sagaId, pricelist.getId(), null, pricelist.getStatus(),
                "CREATED", command.getUserId(), command.getTeamId(), command.getRegion(), startedAt);

        try {
            transition(saga, SagaState.STARTED, "Saga started for pricelist creation");
            Pricelist savedPricelist = collaborationGraphService.createPricelist(pricelist);
            saga.setPricelistId(savedPricelist.getId());
            transition(saga, SagaState.PRICELIST_CREATED, "Neo4j pricelist insert completed");

            LifecycleEventWrittenReply reply = writeLifecycleEvent(saga, startedAt, "NONE", saga.getNewStatus());
            if (reply != null && reply.isSuccess()) {
                transition(saga, SagaState.LIFECYCLE_EVENT_WRITTEN, "Influx lifecycle event write completed");
                transition(saga, SagaState.COMPLETED, "Saga completed");
                return toReply(saga, true, null);
            }

            String errorMessage = reply == null ? "No reply from TimeseriesDatabaseService" : reply.getErrorMessage();
            saga.setErrorMessage(errorMessage);
            compensateCreatedPricelist(saga);
            return toReply(saga, false, errorMessage);
        } catch (Exception ex) {
            saga.setErrorMessage(ex.getMessage());
            compensateCreatedPricelist(saga);
            return toReply(saga, false, ex.getMessage());
        }
    }

    public PricelistOperationReply startChangeStatusSaga(String pricelistId, ChangePricelistStatusCommand command) {
        Instant startedAt = Instant.now();
        String sagaId = UUID.randomUUID().toString();
        if (command == null || command.getStatus() == null || command.getStatus().isBlank()) {
            SagaInstance saga = createSaga(sagaId, pricelistId, null, null,
                    "STATUS_CHANGED", null, null, null, startedAt);
            transition(saga, SagaState.FAILED, "Saga failed because target status is missing");
            return toReply(saga, false, "Target status is required");
        }
        Pricelist existingPricelist = collaborationGraphService.getPricelistById(pricelistId);
        String previousStatus = existingPricelist.getStatus();

        SagaInstance saga = createSaga(sagaId, pricelistId, previousStatus, command.getStatus(),
                "STATUS_CHANGED", command.getUserId(), command.getTeamId(), command.getRegion(), startedAt);

        try {
            transition(saga, SagaState.STARTED, "Saga started for pricelist status change");
            Pricelist updatedPricelist = new Pricelist(
                    existingPricelist.getId(),
                    existingPricelist.getName(),
                    command.getStatus(),
                    existingPricelist.getVersion());
            collaborationGraphService.updatePricelist(pricelistId, updatedPricelist);
            transition(saga, SagaState.STATUS_CHANGED, "Neo4j pricelist status update completed");

            LifecycleEventWrittenReply reply = writeLifecycleEvent(saga, startedAt, previousStatus, saga.getNewStatus());
            if (reply != null && reply.isSuccess()) {
                transition(saga, SagaState.LIFECYCLE_EVENT_WRITTEN, "Influx lifecycle event write completed");
                transition(saga, SagaState.COMPLETED, "Saga completed");
                return toReply(saga, true, null);
            }

            String errorMessage = reply == null ? "No reply from TimeseriesDatabaseService" : reply.getErrorMessage();
            saga.setErrorMessage(errorMessage);
            compensateStatusChange(saga, existingPricelist);
            return toReply(saga, false, errorMessage);
        } catch (Exception ex) {
            saga.setErrorMessage(ex.getMessage());
            compensateStatusChange(saga, existingPricelist);
            return toReply(saga, false, ex.getMessage());
        }
    }

    public PricelistOperationReply startDeletePricelistSaga(String pricelistId, DeletePricelistCommand command) {
        Instant startedAt = Instant.now();
        String sagaId = UUID.randomUUID().toString();
        DeletePricelistCommand safeCommand = command == null ? new DeletePricelistCommand() : command;
        Pricelist existingPricelist = collaborationGraphService.getPricelistById(pricelistId);

        SagaInstance saga = createSaga(sagaId, pricelistId, existingPricelist.getStatus(), "DELETED",
                "DELETED", safeCommand.getUserId(), safeCommand.getTeamId(), safeCommand.getRegion(), startedAt);

        try {
            transition(saga, SagaState.STARTED, "Saga started for pricelist delete");
            collaborationGraphService.deletePricelist(pricelistId);
            transition(saga, SagaState.PRICELIST_DELETED, "Neo4j pricelist delete completed");

            LifecycleEventWrittenReply reply = writeLifecycleEvent(saga, startedAt, existingPricelist.getStatus(), "DELETED");
            if (reply != null && reply.isSuccess()) {
                transition(saga, SagaState.LIFECYCLE_EVENT_WRITTEN, "Influx lifecycle event write completed");
                transition(saga, SagaState.COMPLETED, "Saga completed");
                return toReply(saga, true, null);
            }

            String errorMessage = reply == null ? "No reply from TimeseriesDatabaseService" : reply.getErrorMessage();
            saga.setErrorMessage(errorMessage);
            compensateDeletedPricelist(saga, existingPricelist);
            return toReply(saga, false, errorMessage);
        } catch (Exception ex) {
            saga.setErrorMessage(ex.getMessage());
            compensateDeletedPricelist(saga, existingPricelist);
            return toReply(saga, false, ex.getMessage());
        }
    }

    public SagaInstance getSaga(String sagaId) {
        return sagaStore.get(sagaId);
    }

    private SagaInstance createSaga(String sagaId, String pricelistId, String previousStatus, String newStatus,
                                    String operationType, String userId, String teamId, String region, Instant timestamp) {
        SagaInstance saga = new SagaInstance();
        saga.setSagaId(sagaId);
        saga.setPricelistId(pricelistId);
        saga.setPreviousStatus(previousStatus);
        saga.setNewStatus(newStatus);
        saga.setOperationType(operationType);
        saga.setUserId(userId);
        saga.setTeamId(teamId);
        saga.setRegion(region);
        saga.setTimestamp(timestamp);
        sagaStore.put(sagaId, saga);
        return saga;
    }

    private LifecycleEventWrittenReply writeLifecycleEvent(SagaInstance saga, Instant startedAt, String statusFrom, String statusTo) {
        WriteLifecycleEventCommand eventCommand = new WriteLifecycleEventCommand();
        eventCommand.setSagaId(saga.getSagaId());
        eventCommand.setPricelistId(saga.getPricelistId());
        eventCommand.setUserId(saga.getUserId());
        eventCommand.setTeamId(saga.getTeamId());
        eventCommand.setRegion(saga.getRegion());
        eventCommand.setOperationType(saga.getOperationType());
        eventCommand.setStatusFrom(statusFrom);
        eventCommand.setStatusTo(statusTo);
        eventCommand.setDurationMs(Duration.between(startedAt, Instant.now()).toMillis());
        eventCommand.setSuccess(true);
        eventCommand.setTimestamp(Instant.now());

        Object reply = rabbitTemplate.convertSendAndReceive(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.WRITE_LIFECYCLE_EVENT_ROUTING_KEY,
                eventCommand);
        if (reply instanceof LifecycleEventWrittenReply lifecycleEventWrittenReply) {
            return lifecycleEventWrittenReply;
        }
        if (reply != null) {
            LOGGER.warn("Saga {} received unexpected lifecycle event reply type: {}",
                    saga.getSagaId(), reply.getClass().getName());
        }
        return null;
    }

    private void compensateCreatedPricelist(SagaInstance saga) {
        transition(saga, SagaState.COMPENSATING, "Compensating create saga by deleting pricelist from Neo4j");
        try {
            collaborationGraphService.deletePricelist(saga.getPricelistId());
            transition(saga, SagaState.COMPENSATED, "Created pricelist deleted from Neo4j");
        } catch (Exception compensationError) {
            saga.setErrorMessage(joinErrors(saga.getErrorMessage(), compensationError.getMessage()));
            transition(saga, SagaState.FAILED, "Create compensation failed");
        }
    }

    private void compensateStatusChange(SagaInstance saga, Pricelist originalPricelist) {
        transition(saga, SagaState.COMPENSATING, "Compensating status saga by reverting pricelist status in Neo4j");
        try {
            collaborationGraphService.updatePricelist(originalPricelist.getId(), originalPricelist);
            transition(saga, SagaState.COMPENSATED, "Pricelist status reverted in Neo4j");
        } catch (Exception compensationError) {
            saga.setErrorMessage(joinErrors(saga.getErrorMessage(), compensationError.getMessage()));
            transition(saga, SagaState.FAILED, "Status compensation failed");
        }
    }

    private void compensateDeletedPricelist(SagaInstance saga, Pricelist deletedPricelist) {
        transition(saga, SagaState.COMPENSATING, "Compensating delete saga by restoring pricelist in Neo4j");
        try {
            collaborationGraphService.createPricelist(deletedPricelist);
            transition(saga, SagaState.COMPENSATED, "Deleted pricelist restored in Neo4j");
        } catch (Exception compensationError) {
            saga.setErrorMessage(joinErrors(saga.getErrorMessage(), compensationError.getMessage()));
            transition(saga, SagaState.FAILED, "Delete compensation failed");
        }
    }

    private void transition(SagaInstance saga, SagaState state, String message) {
        saga.setState(state);
        LOGGER.info("Saga {} transitioned to {}: {}", saga.getSagaId(), state, message);
    }

    private PricelistOperationReply toReply(SagaInstance saga, boolean success, String errorMessage) {
        PricelistOperationReply reply = new PricelistOperationReply();
        reply.setSagaId(saga.getSagaId());
        reply.setPricelistId(saga.getPricelistId());
        reply.setState(saga.getState());
        reply.setSuccess(success);
        reply.setErrorMessage(errorMessage);
        return reply;
    }

    private String joinErrors(String first, String second) {
        if (first == null || first.isBlank()) {
            return second;
        }
        if (second == null || second.isBlank()) {
            return first;
        }
        return first + "; compensation error: " + second;
    }
}
