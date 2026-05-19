package infrastructure.web.SSE;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SseService {

    private final List<ClientConnection> clientes = new CopyOnWriteArrayList<>();
    private final List<NotificacaoSSE> notificacoes = new ArrayList<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    public SseService() {
        heartbeatScheduler.scheduleAtFixedRate(this::enviarHeartbeat, 15, 15, TimeUnit.SECONDS);
    }

    public SseEmitter registrarCliente(String clientId) {
        clientes.stream()
                .filter(c -> c.clientId.equals(clientId))
                .findFirst()
                .ifPresent(c -> {
                    try {
                        c.emitter.complete();
                    } catch (Exception ignored) {
                    }
                    clientes.remove(c);
                });

        SseEmitter emitter = new SseEmitter(0L);
        ClientConnection cliente = new ClientConnection(clientId, emitter);
        clientes.add(cliente);

        try{
        List<String> payloads = notificacoes.stream().map(NotificacaoSSE::getJsonPayload).toList();
        cliente.emitter.send(SseEmitter.event()
                .data(payloads)
                .build());
        } catch (Exception e){
            log.error("Erro ao mandar notificação novo cliente " + clientId);
        }
        emitter.onTimeout(() -> removerCliente(cliente));
        emitter.onCompletion(() -> removerCliente(cliente));
        emitter.onError(ex -> removerCliente(cliente));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (Exception e) {
            log.warn("Falha ao enviar evento inicial para {}: {}", clientId, e.getMessage());
            removerCliente(cliente);
        }

        log.info("Cliente {} conectado. Total: {}", clientId, clientes.size());
        return emitter;
    }

    public void removerCliente(String clientId) {
        boolean removed = clientes.removeIf(c -> c.clientId.equals(clientId));
        if (removed) {
            log.info("Cliente {} desconectado. Total: {}", clientId, clientes.size());
        }
    }

    private void removerCliente(ClientConnection connection) {
        boolean removed = clientes.remove(connection);
        if (removed) {
            log.info("Cliente {} desconectado. Total: {}", connection.clientId, clientes.size());
        }
    }

    public void enviarParaTodos(String mensagem) {
        List<ClientConnection> desconectados = new ArrayList<>();

        NotificacaoSSE notif = NotificacaoSSE.from(mensagem);
        if (notif != null) {
            notificacoes.removeIf(n -> n.getId().equals(notif.getId()));
            notificacoes.add(notif);
            log.info("Notificação adicionada/substituída: id={}, total={}", notif.getId(), notificacoes.size());
        } else {
            log.warn("Não foi possível processar a notificação: {}", mensagem);
            return;
        }

        int enviados = 0;
        for (ClientConnection cliente : clientes) {
            try {
                List<String> payloads = notificacoes.stream().map(NotificacaoSSE::getJsonPayload).toList();
                cliente.emitter.send(SseEmitter.event()
                        .data(payloads)
                        .build());
                enviados++;
            } catch (Exception e) {
                log.warn("Erro ao enviar para {}: {}", cliente.clientId, e.getMessage());
                desconectados.add(cliente);
            }
        }
        log.debug("Mensagem enviada para {} clientes", enviados);

        desconectados.forEach(c -> removerCliente(c.clientId));
    }

    private void enviarHeartbeat() {
        List<ClientConnection> desconectados = new ArrayList<>();

        for (ClientConnection cliente : clientes) {
            try {
                cliente.emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            } catch (Exception e) {
                desconectados.add(cliente);
            }
        }

        desconectados.forEach(c -> removerCliente(c.clientId));
    }

    @PreDestroy
    public void shutdown() {
        heartbeatScheduler.shutdownNow();
    }

    public void consumirMensagem(String id) {
        boolean removed = notificacoes.removeIf(n -> n.getId().equals(id));
        if (removed) {
            log.info("Notificação consumida/removida: id={}, restantes={}", id, notificacoes.size());
        } else {
            log.warn("Notificação com id={} não encontrada", id);
        }
    }

    private static class ClientConnection {
        String clientId;
        SseEmitter emitter;

        ClientConnection(String clientId, SseEmitter emitter) {
            this.clientId = clientId;
            this.emitter = emitter;
        }
    }
}
