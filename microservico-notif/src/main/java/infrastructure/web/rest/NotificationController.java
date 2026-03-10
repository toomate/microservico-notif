package infrastructure.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.adapters.notification.NotificationResolver;
import core.application.usecase.notification.ConsumirMensagemUseCase;
import core.domain.shared.interfaces.Notificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final ConsumirMensagemUseCase consumirMensagem;
    private final NotificationResolver notificationResolver;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> notificar(@RequestBody JsonNode payload) {
        log.info("Notificação recebida: {}", payload);

        var notificacaoClass = notificationResolver.resolve(payload);

        if (notificacaoClass.isEmpty()) {
            log.warn("Tipo de notificação desconhecido: {}", payload);
            return ResponseEntity.unprocessableEntity().build();
        }

        try {
            Notificacao notificacao = objectMapper.convertValue(
                    payload,
                    notificacaoClass.get()
            );

            log.info("Notificação desserializada como: {}", notificacao.getClass().getSimpleName());

            consumirMensagem.consumirMensagem(notificacao);

            return ResponseEntity.status(202).build();

        } catch (Exception e) {
            log.error("Erro ao desserializar/processar notificação", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
