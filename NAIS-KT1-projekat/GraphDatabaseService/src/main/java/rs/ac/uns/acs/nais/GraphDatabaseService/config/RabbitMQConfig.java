package rs.ac.uns.acs.nais.GraphDatabaseService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for GraphDatabaseService.
 *
 * Declares all exchanges, queues, and bindings used by both Saga patterns:
 *   - Choreography: services communicate directly through domain events (event-driven).
 *   - Orchestration: the SagaOrchestrator sends commands and waits for replies.
 *
 * RabbitMQ creates missing queues and exchanges on startup if they do not already exist.
 * The same queues are also declared in ElasticSearchDatabaseService -- RabbitMQ treats
 * duplicate declarations as no-ops when parameters match.
 */
@Configuration
public class RabbitMQConfig {

    // -------------------------------------------------------------------------
    // Exchange, queue, and routing-key name constants
    // -------------------------------------------------------------------------

    // -- CHOREOGRAPHY --
    public static final String CHOREOGRAPHY_EXCHANGE       = "saga.choreography.exchange";

    public static final String PURCHASE_CREATED_QUEUE      = "purchase.created.queue";
    public static final String PURCHASE_CREATED_KEY        = "purchase.created";

    public static final String PRODUCT_UPDATED_QUEUE       = "product.updated.queue";
    public static final String PRODUCT_UPDATED_KEY         = "product.updated";

    public static final String PRODUCT_UPDATE_FAILED_QUEUE = "product.update.failed.queue";
    public static final String PRODUCT_UPDATE_FAILED_KEY   = "product.update.failed";

    public static final String PURCHASE_COMPENSATED_QUEUE  = "purchase.compensated.queue";
    public static final String PURCHASE_COMPENSATED_KEY    = "purchase.compensated";

    // -- ORCHESTRATION --
    public static final String ORCHESTRATION_EXCHANGE      = "saga.orchestration.exchange";

    public static final String CREATE_PURCHASE_CMD_QUEUE   = "create.purchase.command.queue";
    public static final String CREATE_PURCHASE_CMD_KEY     = "create.purchase.command";

    public static final String DELETE_PURCHASE_CMD_QUEUE   = "delete.purchase.command.queue";
    public static final String DELETE_PURCHASE_CMD_KEY     = "delete.purchase.command";

    public static final String UPDATE_PRODUCT_CMD_QUEUE    = "update.product.command.queue";
    public static final String UPDATE_PRODUCT_CMD_KEY      = "update.product.command";

    public static final String PURCHASE_CREATED_REPLY_QUEUE  = "purchase.created.reply.queue";
    public static final String PURCHASE_CREATED_REPLY_KEY    = "purchase.created.reply";

    public static final String PURCHASE_DELETED_REPLY_QUEUE  = "purchase.deleted.reply.queue";
    public static final String PURCHASE_DELETED_REPLY_KEY    = "purchase.deleted.reply";

    public static final String PRODUCT_UPDATED_REPLY_QUEUE   = "product.updated.reply.queue";
    public static final String PRODUCT_UPDATED_REPLY_KEY     = "product.updated.reply";

    // =========================================================================
    // Message converter and RabbitTemplate
    // =========================================================================

    /** Converts Java objects to JSON messages and back for both sending and receiving. */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures RabbitTemplate with the JSON converter so all outbound messages
     * are serialized to JSON automatically.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Configures the @RabbitListener container factory with the JSON converter so
     * inbound messages are deserialized into the correct Java type automatically.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // =========================================================================
    // CHOREOGRAPHY -- exchange and bindings
    // =========================================================================

    /** TopicExchange for choreography -- supports wildcard routing keys. */
    @Bean
    public TopicExchange choreographyExchange() {
        return new TopicExchange(CHOREOGRAPHY_EXCHANGE);
    }

    @Bean public Queue purchaseCreatedQueue()     { return QueueBuilder.durable(PURCHASE_CREATED_QUEUE).build(); }
    @Bean public Queue productUpdatedQueue()      { return QueueBuilder.durable(PRODUCT_UPDATED_QUEUE).build(); }
    @Bean public Queue productUpdateFailedQueue() { return QueueBuilder.durable(PRODUCT_UPDATE_FAILED_QUEUE).build(); }
    @Bean public Queue purchaseCompensatedQueue() { return QueueBuilder.durable(PURCHASE_COMPENSATED_QUEUE).build(); }

    @Bean public Binding purchaseCreatedBinding()     { return BindingBuilder.bind(purchaseCreatedQueue()).to(choreographyExchange()).with(PURCHASE_CREATED_KEY); }
    @Bean public Binding productUpdatedBinding()      { return BindingBuilder.bind(productUpdatedQueue()).to(choreographyExchange()).with(PRODUCT_UPDATED_KEY); }
    @Bean public Binding productUpdateFailedBinding() { return BindingBuilder.bind(productUpdateFailedQueue()).to(choreographyExchange()).with(PRODUCT_UPDATE_FAILED_KEY); }
    @Bean public Binding purchaseCompensatedBinding() { return BindingBuilder.bind(purchaseCompensatedQueue()).to(choreographyExchange()).with(PURCHASE_COMPENSATED_KEY); }

    // =========================================================================
    // ORCHESTRATION -- exchange and bindings
    // =========================================================================

    /** DirectExchange for orchestration -- exact routing key matching. */
    @Bean
    public DirectExchange orchestrationExchange() {
        return new DirectExchange(ORCHESTRATION_EXCHANGE);
    }

    // Command queues
    @Bean public Queue createPurchaseCmdQueue()  { return QueueBuilder.durable(CREATE_PURCHASE_CMD_QUEUE).build(); }
    @Bean public Queue deletePurchaseCmdQueue()  { return QueueBuilder.durable(DELETE_PURCHASE_CMD_QUEUE).build(); }
    @Bean public Queue updateProductCmdQueue()   { return QueueBuilder.durable(UPDATE_PRODUCT_CMD_QUEUE).build(); }

    // Reply queues
    @Bean public Queue purchaseCreatedReplyQueue() { return QueueBuilder.durable(PURCHASE_CREATED_REPLY_QUEUE).build(); }
    @Bean public Queue purchaseDeletedReplyQueue() { return QueueBuilder.durable(PURCHASE_DELETED_REPLY_QUEUE).build(); }
    @Bean public Queue productUpdatedReplyQueue()  { return QueueBuilder.durable(PRODUCT_UPDATED_REPLY_QUEUE).build(); }

    // Bindings for command queues
    @Bean public Binding createPurchaseCmdBinding()  { return BindingBuilder.bind(createPurchaseCmdQueue()).to(orchestrationExchange()).with(CREATE_PURCHASE_CMD_KEY); }
    @Bean public Binding deletePurchaseCmdBinding()  { return BindingBuilder.bind(deletePurchaseCmdQueue()).to(orchestrationExchange()).with(DELETE_PURCHASE_CMD_KEY); }
    @Bean public Binding updateProductCmdBinding()   { return BindingBuilder.bind(updateProductCmdQueue()).to(orchestrationExchange()).with(UPDATE_PRODUCT_CMD_KEY); }

    // Bindings for reply queues
    @Bean public Binding purchaseCreatedReplyBinding() { return BindingBuilder.bind(purchaseCreatedReplyQueue()).to(orchestrationExchange()).with(PURCHASE_CREATED_REPLY_KEY); }
    @Bean public Binding purchaseDeletedReplyBinding() { return BindingBuilder.bind(purchaseDeletedReplyQueue()).to(orchestrationExchange()).with(PURCHASE_DELETED_REPLY_KEY); }
    @Bean public Binding productUpdatedReplyBinding()  { return BindingBuilder.bind(productUpdatedReplyQueue()).to(orchestrationExchange()).with(PRODUCT_UPDATED_REPLY_KEY); }
}
