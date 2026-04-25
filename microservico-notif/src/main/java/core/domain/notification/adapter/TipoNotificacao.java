package core.domain.notification.adapter;

import core.domain.notification.Boleto;
import core.domain.notification.Estoque;
import core.domain.notification.Vencimento;
import lombok.Getter;

@Getter
public enum TipoNotificacao {
    estoque("e", Estoque.class),
    boleto("b", Boleto.class),
    vencimento("v", Vencimento.class);


    private final String id;
    private final Class<? extends NotificacaoDeterminada> clazz;

    TipoNotificacao(String id, Class<? extends NotificacaoDeterminada> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

}
