package infrastructure.config;

import core.adapters.notification.web.NotificationWebAdapter;
import core.application.usecase.notification.ConsumirMensagemUseCase;
import core.domain.utility.NotificationResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    ConsumirMensagemUseCase consumirMensagemUseCase(NotificationWebAdapter notificationWebAdapter, NotificationResolver notificationResolver) {
        return new ConsumirMensagemUseCase(notificationWebAdapter, notificationResolver);
    }

    @Bean
    NotificationResolver notificationResolver(){
        return new NotificationResolver();
    };

}
