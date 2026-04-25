package core.domain.notification;

import core.domain.notification.adapter.NotificacaoDeterminada;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Estoque extends NotificacaoDeterminada {
    String nome;
    Integer quantidadeMinima;
    Double quantidadeAtual;
    String unidadeMedida;

    @Override
    public NotificacaoDeterminada validar() {
        return null;
    }

    @Override
    public String toString() {
        if (quantidadeAtual < quantidadeMinima) {
            return "Alerta de estoque para " + nome + " \nQuantidade atual de " + quantidadeAtual + " " + unidadeMedida + "\nQuantidade mínima de " + quantidadeMinima + " " + unidadeMedida;
        }
        return "Estoque de " + nome + " com quantidade atual de " + quantidadeAtual + " " + unidadeMedida + " e quantidade mínima de " + quantidadeMinima + " " + unidadeMedida;
    }
}
