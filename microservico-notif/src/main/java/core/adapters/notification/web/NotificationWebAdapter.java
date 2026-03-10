package core.adapters.notification.web;

import core.domain.shared.interfaces.Notificacao;

public interface NotificationWebAdapter {

    void enviarMensagem(Notificacao mensagem);

}
