package rs.ac.uns.acs.nais.GraphDatabaseService.saga.orchestration;

/**
 * Lifecycle states for a single orchestrated Saga instance.
 *
 * State transition diagram:
 *
 *   STARTED
 *     | (Neo4j creates the PURCHASED relationship)
 *   PURCHASE_CREATED
 *     | (ES increments purchaseCount)
 *   PRODUCT_UPDATED -> COMPLETED
 *
 *   PURCHASE_CREATED (ES reports failure)
 *     |
 *   COMPENSATING
 *     | (Neo4j deletes the PURCHASED relationship)
 *   COMPENSATED -> FAILED
 */
public enum SagaState {

    /** Saga has been created; waiting for the Neo4j service reply. */
    STARTED,

    /** Neo4j successfully created the PURCHASED relationship; waiting for ES update. */
    PURCHASE_CREATED,

    /** ES successfully incremented purchaseCount. */
    PRODUCT_UPDATED,

    /** A failure was detected; compensation command has been sent to Neo4j. */
    COMPENSATING,

    /** Neo4j successfully deleted the PURCHASED relationship (compensation done). */
    COMPENSATED,

    /** Both steps completed successfully -- the saga is done. */
    COMPLETED,

    /** Terminal failure state -- compensation could not complete or a critical error occurred. */
    FAILED
}
