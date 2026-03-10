package core.adapters.notification;

import com.fasterxml.jackson.databind.JsonNode;
import core.domain.shared.interfaces.Notificacao;
import core.domain.notification.NotificacaoEstoque;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class NotificationResolver {

    private final Map<String, Class<? extends Notificacao>> registry = Map.of(
            "estoque", NotificacaoEstoque.class
    );

    public Optional<Class<? extends Notificacao>> resolve(JsonNode payload) {
        if (payload == null || payload.isNull()) {
            return Optional.empty();
        }

        if (payload.has("tipo") && payload.get("tipo").isTextual()) {
            String tipo = payload.get("tipo").asText();
            if (registry.containsKey(tipo)) {
                return Optional.of(registry.get(tipo));
            }
        }

        if (payload.has("nomeProduto") && payload.has("idProduto")) {
            return Optional.of(NotificacaoEstoque.class);
        }

        return Optional.empty();
    }
}
