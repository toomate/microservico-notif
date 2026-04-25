package core.domain.notification;

import core.domain.notification.adapter.NotificacaoDeterminada;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Boleto extends NotificacaoDeterminada {
    String nome;
    String descricao;
    LocalDateTime vencimento;
    Double valor;

    @Override
    public Boleto validar() {
        return null;
    }

    @Override
    public String toString() {
        return "Boleto no valor de " + valor + " para " + nome + " com vencimento em " + vencimento;
    }
}
