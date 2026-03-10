package core.domain.notification;

import core.domain.shared.exception.ArgumentoInvalidoException;
import core.domain.shared.interfaces.Notificacao;

public record NotificacaoEstoque(
        String nomeProduto,
        int idProduto,
        int quantidadeAtual,
        int quantidadeMinima,
        String unidadeMedidaz
) implements Notificacao {
    @Override
    public void validar() {
        if (quantidadeMinima < quantidadeAtual){
            throw new ArgumentoInvalidoException("Quantidade minima maior do que quantidade atual");
        }
    }
}
