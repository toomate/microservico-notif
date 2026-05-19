package infrastructure.web.SSE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class NotificacaoSSE {
    private String id;
    private String jsonPayload;

    public static NotificacaoSSE from(String jsonPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonPayload);
            String id = node.get("id").asText();
            return new NotificacaoSSE(id, jsonPayload);
        } catch (Exception e) {
            log.warn("Falha ao extrair ID da notificação: {}", e.getMessage());
            return null;
        }
    }
}
