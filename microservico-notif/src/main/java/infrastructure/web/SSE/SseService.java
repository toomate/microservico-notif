package infrastructure.web.SSE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final List<ClientConnection> clientes = new CopyOnWriteArrayList<>();
    private final List<String> notificacoes = new ArrayList<>();

    public SseEmitter registrarCliente(String clientId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutos de timeout

        ClientConnection cliente = new ClientConnection(clientId, emitter);
        clientes.add(cliente);

        log.info("Cliente {} conectado. Total: {}", clientId, clientes.size());
        return emitter;
    }

    public void removerCliente(String clientId) {
        clientes.removeIf(c -> c.clientId.equals(clientId));
        log.info("Cliente {} desconectado. Total: {}", clientId, clientes.size());
    }

    public void enviarParaTodos(String mensagem) {
        List<ClientConnection> desconectados = new ArrayList<>();

        notificacoes.add(mensagem);

        for (ClientConnection cliente : clientes) {
            try {
                cliente.emitter.send(SseEmitter.event()
                        .data(notificacoes)
                        .build());
                log.debug("Mensagem enviada para {}", cliente.clientId);
            } catch (Exception e) {
                log.warn("Erro ao enviar para {}: {}", cliente.clientId, e.getMessage());
                desconectados.add(cliente);
            }
        }

        desconectados.forEach(c -> removerCliente(c.clientId));
    }

    public void consumirMensagem(Integer id) {
        notificacoes.remove(id);
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
