package rs.ac.uns.acs.nais.GraphDatabaseService.saga.choreography.event;

import java.time.LocalDateTime;

/**
 * Događaj koji Neo4j servis objavljuje nakon uspešnog kreiranja PURCHASED relacije.
 * ElasticSearch servis sluša ovaj događaj i ažurira purchaseCount na proizvodu.
 *
 * Polja:
 *   sagaId       – jedinstveni identifikator ove saga instance (UUID kao String)
 *   customerId   – ID kupca koji je inicirao kupovinu
 *   productId    – ID proizvoda koji je kupljen
 *   numberOfItems – broj kupljenih jedinica
 *   timestamp    – vreme nastanka događaja
 */
public class PurchaseCreatedEvent {

    private String sagaId;
    private Long customerId;
    private String productId;
    private Integer numberOfItems;
    private LocalDateTime timestamp;

    public PurchaseCreatedEvent() {}

    public PurchaseCreatedEvent(String sagaId, Long customerId, String productId,
                                Integer numberOfItems, LocalDateTime timestamp) {
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.productId = productId;
        this.numberOfItems = numberOfItems;
        this.timestamp = timestamp;
    }

    public String getSagaId()             { return sagaId; }
    public void setSagaId(String sagaId)  { this.sagaId = sagaId; }

    public Long getCustomerId()                  { return customerId; }
    public void setCustomerId(Long customerId)   { this.customerId = customerId; }

    public String getProductId()                 { return productId; }
    public void setProductId(String productId)   { this.productId = productId; }

    public Integer getNumberOfItems()                   { return numberOfItems; }
    public void setNumberOfItems(Integer numberOfItems) { this.numberOfItems = numberOfItems; }

    public LocalDateTime getTimestamp()                    { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp)      { this.timestamp = timestamp; }
}
