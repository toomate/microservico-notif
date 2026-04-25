package infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "broker")
public record RabbitPropertiesConfiguration(
        Exchange exchange,
        Queue queue
) {
    public record Exchange(String name) {
    }

    public record Queue(String name) {
    }
}