package timeseries.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "collaborative-pricelist-saga-exchange";
    public static final String WRITE_LIFECYCLE_EVENT_QUEUE = "timeseries.write-lifecycle-event.command";
    public static final String LIFECYCLE_EVENT_REPLY_QUEUE = "collaborative-pricelist.lifecycle-event.reply";
    public static final String WRITE_LIFECYCLE_EVENT_ROUTING_KEY = "timeseries.lifecycle-event.write";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue writeLifecycleEventQueue() {
        return new Queue(WRITE_LIFECYCLE_EVENT_QUEUE, true);
    }

    @Bean
    public Queue lifecycleEventReplyQueue() {
        return new Queue(LIFECYCLE_EVENT_REPLY_QUEUE, true);
    }

    @Bean
    public Binding writeLifecycleEventBinding(Queue writeLifecycleEventQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(writeLifecycleEventQueue)
                .to(sagaExchange)
                .with(WRITE_LIFECYCLE_EVENT_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("collab.saga.dto");
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}
