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

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/conectar/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter conectar(@PathVariable String id) {
        log.info("Cliente tentando conectar com ID: {}", id);
        return sseService.registrarCliente(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/deletar/{idInsumo}")
    public ResponseEntity<Void> deletar(@PathVariable String idInsumo){
        log.info("Cliente tentando consumir a notificação com ID: {}", idInsumo);
        sseService.consumirMensagem(idInsumo);
        return ResponseEntity.status(201).build();
    }

}
