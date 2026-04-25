package core.domain.notification;

import core.domain.notification.adapter.NotificacaoDeterminada;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Vencimento extends NotificacaoDeterminada {
    String nome;
    LocalDateTime vencimento;

    @Override
    public Vencimento validar() {
        return null;
    }
}
