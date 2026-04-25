package core.adapters.notification.web;

import core.domain.notification.adapter.NotificacaoDeterminada;
import infrastructure.web.SSE.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebAdapterImpl implements NotificationWebAdapter {

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @Override
    public void enviarMensagem(NotificacaoDeterminada notificacao) {
        try {
            // Serializa a notificação determinada em JSON para o cliente SSE
            String mensagemJson = objectMapper.writeValueAsString(notificacao);

            log.info("Enviando notificação via SSE: {}", mensagemJson);

            sseService.enviarParaTodos(mensagemJson);

        } catch (MessageConversionException e){
            log.error("Erro ao tentar desserializar a mensagem: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            log.error("Erro ao enviar notificação via SSE: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
