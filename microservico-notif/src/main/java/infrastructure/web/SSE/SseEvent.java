package infrastructure.web.SSE;

import lombok.Getter;

/**
 * Classe simples para representar uma notificação.
 * Pode ser uma String, um JSON, ou um objeto.
 */
@Getter
public class SseEvent {
    private final String mensagem;
    private final long timestamp;

    public SseEvent(String mensagem) {
        this.mensagem = mensagem;
        this.timestamp = System.currentTimeMillis();
    }


    @Override
    public String toString() {
        return mensagem;
    }
}
