package core.domain.notification;

import core.domain.notification.adapter.NotificacaoDeterminada;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
public class Vencimento extends NotificacaoDeterminada {
    String nome;
    LocalDateTime vencimento;

    @Override
    public Vencimento validar() {
        return null;
    }
}
