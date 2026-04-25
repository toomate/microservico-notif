package core.domain.notification;


import java.time.LocalDateTime;


public record NotificacaoAgnostico(
        String id,
        LocalDateTime timestamp,
        Object payload
) {
}
