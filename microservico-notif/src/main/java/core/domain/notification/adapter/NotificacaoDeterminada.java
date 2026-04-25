package core.domain.notification.adapter;

import java.time.LocalDateTime;

public abstract class NotificacaoDeterminada {
    String id;
    LocalDateTime timestamp;

    public abstract NotificacaoDeterminada validar();

}
