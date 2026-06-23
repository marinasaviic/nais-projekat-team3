package collab.controller;

import collab.saga.dto.ChangePricelistStatusCommand;
import collab.saga.dto.CreatePricelistCommand;
import collab.saga.dto.DeletePricelistCommand;
import collab.saga.dto.PricelistOperationReply;
import collab.saga.model.SagaInstance;
import collab.saga.service.SagaOrchestrator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collaborative-pricelists")
public class CollaborativePricelistSagaController {

    private final SagaOrchestrator sagaOrchestrator;

    public CollaborativePricelistSagaController(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @PostMapping
    public ResponseEntity<PricelistOperationReply> createPricelist(@RequestBody CreatePricelistCommand command) {
        PricelistOperationReply reply = sagaOrchestrator.startCreatePricelistSaga(command);
        return ResponseEntity.status(reply.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(reply);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PricelistOperationReply> changeStatus(@PathVariable String id,
                                                                @RequestBody ChangePricelistStatusCommand command) {
        PricelistOperationReply reply = sagaOrchestrator.startChangeStatusSaga(id, command);
        return ResponseEntity.status(reply.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(reply);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PricelistOperationReply> deletePricelist(@PathVariable String id,
                                                                   @RequestParam(required = false) String userId,
                                                                   @RequestParam(required = false) String teamId,
                                                                   @RequestParam(required = false) String region) {
        PricelistOperationReply reply = sagaOrchestrator.startDeletePricelistSaga(
                id,
                new DeletePricelistCommand(userId, teamId, region));
        return ResponseEntity.status(reply.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(reply);
    }

    @GetMapping("/sagas/{sagaId}")
    public ResponseEntity<SagaInstance> getSaga(@PathVariable String sagaId) {
        SagaInstance saga = sagaOrchestrator.getSaga(sagaId);
        return saga == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(saga);
    }
}
