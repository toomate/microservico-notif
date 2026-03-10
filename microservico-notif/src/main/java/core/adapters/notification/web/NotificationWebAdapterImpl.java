package core.adapters.notification.web;

import infrastructure.web.SSE.SseService;
import core.domain.shared.interfaces.Notificacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebAdapterImpl implements NotificationWebAdapter {

    private final SseService sseService;

    @Override
    public void enviarMensagem(Notificacao notificacao) {
        try {
            String mensagem = formatarMensagem(notificacao);

            log.info("Enviando notificação via SSE: {}", mensagem);

            sseService.enviarParaTodos(mensagem);

        } catch (Exception e) {
            log.error("Erro ao enviar notificação via SSE", e);
            throw new RuntimeException("Erro ao enviar notificação", e);
        }
    }

    private String formatarMensagem(Notificacao notificacao) {
        return String.format(
            "📢 Notificação: %s (Timestamp: %s)",
            notificacao.toString(),
            System.currentTimeMillis()
        );
    }
}
