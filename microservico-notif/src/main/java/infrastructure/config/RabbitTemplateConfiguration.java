package infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitPropertiesConfiguration.class)
public class RabbitTemplateConfiguration {

    private final RabbitPropertiesConfiguration properties;

    @Bean
    public Declarables rabbitDeclarables() {
        FanoutExchange exchange = new FanoutExchange(properties.exchange().name());

        Queue queue = QueueBuilder
                .durable(properties.queue().name())
                .build();

        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange);

        return new Declarables(exchange, queue, binding);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
