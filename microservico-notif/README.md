# Microserviço de Notificações - SSE

## 🏗️ Arquitetura Clean

```
POST /notification (JSON)
    ↓
NotificationController (infrastructure)
    ↓ resolve tipo + desserializa
ConsumirMensagemUseCase (application)
    ↓ valida + chama adapter
NotificationWebAdapterImpl (core - adapter)
    ↓ formata mensagem
SseService (infrastructure)
    ↓ envia para clientes
Clientes SSE ✅ recebem em tempo real
```

## 🚀 Executar

```bash
mvn spring-boot:run
```

## 🧪 Testar

**Cliente (navegador - 3 abas):**
```
http://localhost:8080/sse-simples.html
```

**Enviar notificação (PowerShell):**
```powershell
curl -X POST http://localhost:8080/notification `
  -H "Content-Type: application/json" `
  -d '{
    "nomeProduto": "Notebook",
    "idProduto": 1,
    "quantidadeAtual": 5,
    "quantidadeMinima": 10
  }'
```

## 📦 Build

```bash
mvn clean package
java -jar target/microservico-notif-1.0-SNAPSHOT.jar
```

## 🔧 Se Porta 8080 estiver em uso

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

## 📍 Arquivos Principais

- **Core (Domínio/Adapter):**
  - `src/main/java/core/adapters/notification/web/NotificationWebAdapter.java` - Interface do adapter
  - `src/main/java/core/adapters/notification/web/NotificationWebAdapterImpl.java` - Implementação (envia SSE)
  - `src/main/java/core/application/usecase/notification/ConsumirMensagemUseCase.java` - UseCase

- **Infrastructure (REST/SSE):**
  - `src/main/java/infrastructure/web/rest/NotificationController.java` - Endpoint POST /notification
  - `src/main/java/infrastructure/web/SSE/SseService.java` - Gerencia clientes SSE
  - `src/main/java/infrastructure/web/SSE/ServerSideEventImplementation.java` - Endpoints GET/POST SSE
  - `src/main/resources/static/sse-simples.html` - Cliente web

## 🔌 Endpoints

- `POST /notification` - Recebe notificação (entrada principal)
- `GET /sse/conectar?clientId=user1` - Cliente se conecta ao SSE
- `POST /sse/enviar` - Debug: envia notificação direto (sem passar por NotificationController)

## ✨ Fluxo Completo

1. **Admin envia:** `POST /notification` com JSON
2. **NotificationController** recebe e valida
3. **ConsumirMensagemUseCase** processa
4. **NotificationWebAdapterImpl** envia via SSE
5. **SseService** distribui para clientes
6. **Clientes conectados** recebem instantaneamente
