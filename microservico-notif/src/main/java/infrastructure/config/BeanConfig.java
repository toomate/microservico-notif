package infrastructure.config;

import core.adapters.notification.web.NotificationWebAdapter;
import core.application.usecase.notification.ConsumirMensagemUseCase;
import core.domain.utility.NotificationResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    ConsumirMensagemUseCase consumirMensagemUseCase(NotificationWebAdapter notificationWebAdapter, NotificationResolver notificationResolver) {
        return new ConsumirMensagemUseCase(notificationWebAdapter, notificationResolver);
    }

    @Bean
    NotificationResolver notificationResolver(){
        return new NotificationResolver();
    };

}
