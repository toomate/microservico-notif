package core.adapters.notification.rabbit;

import core.application.usecase.notification.ConsumirMensagemUseCase;
import core.domain.notification.NotificacaoAgnostico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRabbitListener {

    private final ConsumirMensagemUseCase consumirMensagemUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${broker.queue.name:example.queue}")
    public void receive(String payload) {
        try {
            log.info("Mensagem recebida do broker: {}", payload);

            // Tenta desserializar para NotificacaoAgnostico; espera JSON com campos id, timestamp e payload
            NotificacaoAgnostico notificacao = objectMapper.readValue(payload, NotificacaoAgnostico.class);

            consumirMensagemUseCase.consumirMensagem(notificacao);

            log.info("Mensagem processada com sucesso pelo usecase");
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do broker: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
