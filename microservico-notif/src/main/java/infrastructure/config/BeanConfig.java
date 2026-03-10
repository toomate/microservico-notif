package infrastructure.config;

import core.adapters.notification.web.NotificationWebAdapter;
import core.application.usecase.notification.ConsumirMensagemUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    ConsumirMensagemUseCase consumirMensagemUseCase(NotificationWebAdapter notificationWebAdapter) {
        return new ConsumirMensagemUseCase(notificationWebAdapter);
    }

}
