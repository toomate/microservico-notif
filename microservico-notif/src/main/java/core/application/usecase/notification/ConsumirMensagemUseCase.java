package core.application.usecase.notification;

import core.adapters.notification.web.NotificationWebAdapter;
import core.domain.shared.interfaces.Notificacao;

public class ConsumirMensagemUseCase {

    public ConsumirMensagemUseCase(NotificationWebAdapter notificationWebAdapter) {
        this.notificationWebAdapter = notificationWebAdapter;
    }

    private final NotificationWebAdapter notificationWebAdapter;

    public void consumirMensagem(Notificacao notificacao) {

        notificacao.validar();

        notificationWebAdapter.enviarMensagem(notificacao);
    }

}
