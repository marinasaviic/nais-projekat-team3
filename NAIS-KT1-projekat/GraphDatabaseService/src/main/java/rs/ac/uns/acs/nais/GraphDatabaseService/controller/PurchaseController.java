package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.PurchaseService;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.SagaInstance;
import rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration.SagaOrchestrator;

import java.util.Map;

/**
 * REST controller exposing endpoints to initiate product purchases via the Saga pattern.
 *
 * Endpoints:
 *   POST /api/purchase              -- starts a choreography-based Saga
 *   POST /api/purchase/orchestrated -- starts an orchestration-based Saga
 *   GET  /api/purchase/status/{sagaId} -- polls the state of an orchestrated Saga
 *
 * Request body for both POST endpoints:
 * {
 *   "customerId": 123,
 *   "productId": "prod-abc",
 *   "numberOfItems": 2
 * }
 */
@Slf4j
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SagaOrchestrator sagaOrchestrator;

    public PurchaseController(PurchaseService purchaseService, SagaOrchestrator sagaOrchestrator) {
        this.purchaseService = purchaseService;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    // =========================================================================
    // Choreography Saga
    // =========================================================================

    /**
     * Starts the choreography-based Saga for a product purchase.
     *
     * Flow:
     *   1. Neo4j creates the PURCHASED relationship and publishes PurchaseCreatedEvent.
     *   2. ES picks up the event and increments purchaseCount.
     *   3. If ES fails, it publishes ProductUpdateFailedEvent.
     *   4. CompensationListener receives the failure and deletes the Neo4j relationship.
     *
     * @param request map containing customerId, productId, numberOfItems
     * @return sagaId of the newly created saga instance
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> startChoreographySaga(
            @RequestBody Map<String, Object> request) {

        log.info("[CONTROLLER] POST /api/purchase -- choreography saga -- request: {}", request);

        try {
            Long customerId = Long.valueOf(request.get("customerId").toString());
            String productId = request.get("productId").toString();
            Integer numberOfItems = Integer.valueOf(request.get("numberOfItems").toString());

            String sagaId = purchaseService.createPurchase(customerId, productId, numberOfItems);

            log.info("[CONTROLLER] Choreography saga started -- sagaId={}", sagaId);
            return ResponseEntity.ok(Map.of(
                    "sagaId", sagaId,
                    "status", "STARTED",
                    "message", "Choreography saga started. Follow logs for execution details."));

        } catch (Exception e) {
            log.error("[CONTROLLER] ERROR starting choreography saga: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // Orchestration Saga
    // =========================================================================

    /**
     * Starts the orchestration-based Saga for a product purchase.
     *
     * Flow:
     *   1. SagaOrchestrator sends CreatePurchaseCommand to Neo4j CommandListener.
     *   2. Neo4j creates the relationship and sends PurchaseCreatedReply.
     *   3. SagaOrchestrator sends UpdateProductCommand to ES CommandListener.
     *   4. ES increments purchaseCount and sends ProductUpdatedReply.
     *   5a. Success: saga state = COMPLETED.
     *   5b. Failure: SagaOrchestrator sends DeletePurchaseCommand (compensation).
     *
     * @param request map containing customerId, productId, numberOfItems
     * @return sagaId of the newly created saga instance
     */
    @PostMapping("/orchestrated")
    public ResponseEntity<Map<String, String>> startOrchestratedSaga(
            @RequestBody Map<String, Object> request) {

        log.info("[CONTROLLER] POST /api/purchase/orchestrated -- orchestration saga -- request: {}", request);

        try {
            Long customerId = Long.valueOf(request.get("customerId").toString());
            String productId = request.get("productId").toString();
            Integer numberOfItems = Integer.valueOf(request.get("numberOfItems").toString());

            String sagaId = sagaOrchestrator.startSaga(customerId, productId, numberOfItems);

            log.info("[CONTROLLER] Orchestration saga started -- sagaId={}", sagaId);
            return ResponseEntity.ok(Map.of(
                    "sagaId", sagaId,
                    "status", "STARTED",
                    "message", "Orchestration saga started. Use GET /api/purchase/status/" + sagaId + " to poll state."));

        } catch (Exception e) {
            log.error("[CONTROLLER] ERROR starting orchestration saga: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // Saga status polling
    // =========================================================================

    /**
     * Returns the current state of an orchestrated Saga instance.
     * Useful for status polling and debugging after a POST to /orchestrated.
     *
     * @param sagaId saga identifier returned by the POST endpoint
     * @return JSON with saga state, or HTTP 404 if the sagaId is not found
     */
    @GetMapping("/status/{sagaId}")
    public ResponseEntity<?> getSagaStatus(@PathVariable String sagaId) {
        log.info("[CONTROLLER] GET /api/purchase/status/{} -- polling saga state", sagaId);

        SagaInstance instance = sagaOrchestrator.getSagaStatus(sagaId);
        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Saga with ID=" + sagaId + " not found"));
        }

        return ResponseEntity.ok(Map.of(
                "sagaId", instance.getSagaId(),
                "state", instance.getState().name(),
                "customerId", instance.getCustomerId().toString(),
                "productId", instance.getProductId(),
                "numberOfItems", instance.getNumberOfItems().toString(),
                "createdAt", instance.getCreatedAt().toString()));
    }
}
