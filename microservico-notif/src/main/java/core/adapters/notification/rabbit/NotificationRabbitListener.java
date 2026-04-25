package core.adapters.notification.rabbit;

import core.application.usecase.notification.ConsumirMensagemUseCase;
import core.domain.notification.NotificacaoAgnostico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRabbitListener {

    private final ConsumirMensagemUseCase consumirMensagemUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${broker.queue.name:example.queue}")
    public void receive(Message message) {
        try {
            // Extrai o payload como bytes da mensagem
            byte[] body = message.getBody();
            String jsonPayload = new String(body, "UTF-8");

            log.info("Mensagem recebida do broker: {}", jsonPayload);

            consumirMensagemUseCase.consumirMensagem(jsonPayload);

            log.info("Mensagem processada com sucesso pelo usecase");
        } catch (Exception e) {
            // Log completo da exceção para facilitar depuração
            log.error("Erro ao processar mensagem do broker", e);
            throw new RuntimeException(e);
        }
    }
}
