package core.application.usecase.notification;

import core.adapters.notification.web.NotificationWebAdapter;
import core.domain.notification.NotificacaoAgnostico;
import core.domain.notification.adapter.NotificacaoDeterminada;
import core.domain.utility.NotificationResolver;

public class ConsumirMensagemUseCase {

    private final NotificationWebAdapter notificationWebAdapter;
    private final NotificationResolver notificationResolver;


    public ConsumirMensagemUseCase(NotificationWebAdapter notificationWebAdapter, NotificationResolver notificationResolver) {
        this.notificationWebAdapter = notificationWebAdapter;
        this.notificationResolver = notificationResolver;
    }


    public void consumirMensagem(NotificacaoAgnostico notificacao) {

        NotificacaoDeterminada notificacaoDeterminada = notificationResolver.resolve(notificacao);

        notificationWebAdapter.enviarMensagem(notificacaoDeterminada);
    }

}
