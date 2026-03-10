package infrastructure.web.SSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class ServerSideEventImplementation {

    private final SseService sseService;

    @GetMapping(value = "/conectar", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter conectar(@RequestParam(required = false) String clientId) {
        String id = clientId != null ? clientId : UUID.randomUUID().toString();
        log.info("Cliente tentando conectar com ID: {}", id);
        return sseService.registrarCliente(id);
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarNotificacao(@RequestBody String mensagem) {
        log.info("Enviando notificação para todos os clientes");
        sseService.enviarParaTodos(mensagem);
        return ResponseEntity.ok("Notificação enviada");
    }
}
