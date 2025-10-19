# 📮 DocChain Postman Collection

Collection completa de APIs para os microsserviços **User Service** e **Document Service** do projeto DocChain.

## 📦 Arquivos Gerados

- **`docchain-collection.postman_collection.json`** - Collection principal com todos os endpoints
- **`docchain-environment.postman_environment.json`** - Variáveis de ambiente pré-configuradas

## 🚀 Como Importar no Postman

### Opção 1: Interface Gráfica

1. Abra o Postman
2. Clique em **"Import"** (canto superior esquerdo)
3. Selecione os arquivos:
   - `docchain-collection.postman_collection.json`
   - `docchain-environment.postman_environment.json`
4. Clique em **"Import"**
5. Selecione o environment **"DocChain - Local Development"** no dropdown superior direito

### Opção 2: Drag & Drop

1. Arraste os arquivos `.json` diretamente para a janela do Postman
2. Confirme a importação
3. Selecione o environment apropriado

## 🏗️ Estrutura da Collection

```
📁 DocChain Microservices Collection
├── 📂 User Service (Port 8081)
│   ├── 📄 Create User
│   └── 📄 Create Document for User (User → Document Service)
│
├── 📂 Document Service (Port 8082)
│   └── 📄 Create Document (Direct)
│
├── 📂 Integration Examples
│   ├── 📂 Full Flow - Create User and Document
│   │   ├── Step 1 - Create User
│   │   └── Step 2 - Create Document via User Service
│   └── 📄 Microservices Communication Test
│
└── 📂 Health Checks
    ├── 📄 User Service Health
    ├── 📄 Document Service Health
    └── 📄 Eureka Discovery Status
```

## 🔧 Variáveis de Ambiente

As seguintes variáveis são configuradas automaticamente:

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `user_service_url` | `http://localhost:8081` | Base URL do User Service |
| `document_service_url` | `http://localhost:8082` | Base URL do Document Service |
| `eureka_url` | `http://localhost:8761` | URL do Eureka Discovery |
| `user_id` | (vazio) | Preenchido automaticamente após criar usuário |
| `document_id` | (vazio) | Preenchido automaticamente após criar documento |
| `integration_user_id` | (vazio) | Usado no fluxo de integração |

## 📝 Exemplos de Uso

### 1️⃣ Criar um Usuário

**Endpoint:** `POST /users`

**Request Body:**
```json
{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
}
```

**Response (201 Created):**
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": null,
    "fullName": "João Silva",
    "email": {
        "value": "joao.silva@example.com"
    },
    "passwordHash": null,
    "createdAt": "2025-10-18T14:30:00",
    "documents": []
}
```

> 💡 **Dica:** O `user_id` é automaticamente salvo nas variáveis de ambiente!

---

### 2️⃣ Criar Documento para Usuário (Comunicação entre Microsserviços)

**Endpoint:** `POST /users/{userId}/documents`

**Request Body:**
```json
{
    "title": "Contrato de Prestação de Serviços",
    "content": "Este contrato estabelece os termos e condições..."
}
```

**Response (201 Created):**
```json
{
    "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "title": "Contrato de Prestação de Serviços",
    "content": "Este contrato estabelece os termos e condições...",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "CREATED",
    "createdAt": "2025-10-18T15:45:30+00:00"
}
```

**🔄 Fluxo Interno:**
```
Client
  ↓
User Service (8081)
  ├── Valida que usuário existe
  ├── Prepara DocumentRequest
  └── → RestClient
      ↓
Document Service (8082)
  ├── Cria documento
  ├── Salva no PostgreSQL
  └── → Retorna Document
      ↓
User Service
  └── → Mapeia para DocumentResponseDto
      ↓
Client (recebe response)
```

---

### 3️⃣ Criar Documento Diretamente (Sem User Service)

**Endpoint:** `POST /api/v1/documents`

**Request Body:**
```json
{
    "title": "Manual do Usuário",
    "content": "Este manual contém instruções detalhadas...",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (201 Created):**
```json
{
    "id": "9b2c3f4a-1234-5678-90ab-cdef12345678",
    "fileName": null,
    "title": "Manual do Usuário",
    "content": "Este manual contém instruções detalhadas...",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "CREATED",
    "privacyStatus": null,
    "createdAt": "2025-10-18T16:20:45+00:00",
    "updatedAt": null,
    "format": null,
    "version": 0
}
```

---

## 🧪 Testando o Fluxo Completo

Use a pasta **"Integration Examples"** para testar o fluxo completo:

### Opção A: Manualmente

1. Executa: `User Service → Create User`
   - Anota o `user_id` retornado
2. Executa: `User Service → Create Document for User`
   - Usa o `user_id` do passo anterior

### Opção B: Fluxo Automatizado

1. Executa: `Integration Examples → Full Flow → Step 1 - Create User`
   - `user_id` é salvo automaticamente como `integration_user_id`
2. Executa: `Integration Examples → Full Flow → Step 2 - Create Document via User Service`
   - Usa automaticamente o `integration_user_id` do passo anterior

### Opção C: Teste de Comunicação

Executa: `Integration Examples → Microservices Communication Test`
- Valida que a comunicação User → Document está funcionando
- Testa serialização/deserialização
- Verifica mapeamento de DTOs
- Logs detalhados no Console do Postman

---

## ✅ Testes Automáticos

Cada request inclui **scripts de teste automáticos**:

### User Service - Create User
```javascript
✓ User created successfully
✓ Response contains user ID
✓ Response contains email
✓ Response time is acceptable
```

### User Service - Create Document for User
```javascript
✓ Document created for user
✓ Document has valid owner ID
✓ Response contains document data
✓ Response time is acceptable
```

### Document Service - Create Document
```javascript
✓ Document created successfully
✓ Document status is CREATED
✓ Response contains all required fields
✓ Response time is acceptable
```

### Communication Test
```javascript
✓ User Service is accessible
✓ Response contains document data
✓ Document was created in Document Service
✓ Microservices communication successful
```

---

## 🔍 Recursos Avançados

### 1. Scripts de Pré-Requisição
Validam que variáveis necessárias existem antes de executar a request.

### 2. Scripts de Teste
Automatizam validações e salvam IDs retornados em variáveis de ambiente.

### 3. Console Logs
Logs detalhados aparecem no **Console do Postman** (View → Show Postman Console).

Exemplo:
```
🚀 DocChain API Request
📍 Endpoint: http://localhost:8081/users/550e8400.../documents
🔧 Method: POST
✅ User ID: 550e8400-e29b-41d4-a716-446655440000
✅ Document ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7
✅ Communication Test Results:
   📡 User Service → Document Service: SUCCESS
   📄 Document ID: 7c9e6679-7425-40de-944b-e07fc1f90ae7
   👤 Owner ID: 550e8400-e29b-41d4-a716-446655440000
   📊 Status: CREATED
```

### 4. Response Examples
Cada request inclui exemplos de:
- ✅ Success responses
- ❌ Error responses (validation, not found, etc.)

---

## 🏥 Health Checks

Endpoints de monitoramento via Spring Boot Actuator:

| Service | Endpoint | Port |
|---------|----------|------|
| User Service | `/actuator/health` | 8081 |
| Document Service | `/actuator/health` | 8082 |
| Eureka Discovery | `/` | 8761 |

**Response Esperado:**
```json
{
    "status": "UP"
}
```

---

## 🛠️ Pré-Requisitos

### Serviços Rodando

Certifique-se de que os seguintes serviços estão em execução:

```bash
# PostgreSQL
docker-compose up -d postgres-user postgres-document

# Eureka Discovery Service
cd discovery-service
./mvnw spring-boot:run

# Document Service
cd document
./mvnw spring-boot:run

# User Service
cd user
./mvnw spring-boot:run
```

### Portas Utilizadas

| Serviço | Porta |
|---------|-------|
| User Service | 8081 |
| Document Service | 8082 |
| Eureka Discovery | 8761 |
| PostgreSQL (Users) | 5432 |
| PostgreSQL (Documents) | 5433 |

---

## 🐛 Troubleshooting

### ❌ Erro: Connection Refused

**Problema:** Serviço não está rodando ou porta incorreta

**Solução:**
```bash
# Verificar se serviços estão rodando
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### ❌ Erro: User not found

**Problema:** `user_id` não existe ou está incorreto

**Solução:**
1. Execute primeiro: `User Service → Create User`
2. Verifique que o `user_id` foi salvo nas variáveis de ambiente
3. Ou copie manualmente o `id` do response

### ❌ Erro: Owner ID must be set

**Problema:** Requisição direta ao Document Service sem `ownerId`

**Solução:**
```json
{
    "title": "Título",
    "content": "Conteúdo",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000"  // ✅ Obrigatório
}
```

### ❌ Erro: Validation failed

**Problema:** Campos obrigatórios vazios ou inválidos

**Solução:**
```json
{
    "fullName": "Nome Completo",  // ✅ Não pode ser vazio
    "email": "email@example.com"  // ✅ Não pode ser vazio
}
```

---

## 📊 Arquitetura dos Microsserviços

```
┌─────────────┐
│   Client    │
│  (Postman)  │
└──────┬──────┘
       │
       │ HTTP/REST
       │
┌──────▼──────────────────────────────────────────────────┐
│                    User Service (8081)                   │
│  ┌────────────────────────────────────────────────────┐ │
│  │ UserController                                      │ │
│  │  - POST /users                                      │ │
│  │  - POST /users/{id}/documents                       │ │
│  └──────────────┬──────────────────────────────────────┘ │
│                 │                                          │
│  ┌──────────────▼──────────────┐                         │
│  │ UserRegistrationService     │                         │
│  │ UserDocumentService         │                         │
│  └──────────────┬──────────────┘                         │
│                 │                                          │
│  ┌──────────────▼──────────────┐                         │
│  │ DocumentServiceClient       │ ◄── RestClient           │
│  │ (HttpServiceProxyFactory)   │                         │
│  └──────────────┬──────────────┘                         │
│                 │                                          │
│                 │ HTTP (RestClient)                       │
│                 │                                          │
└─────────────────┼──────────────────────────────────────────┘
                  │
                  │
┌─────────────────▼──────────────────────────────────────────┐
│                  Document Service (8082)                   │
│  ┌────────────────────────────────────────────────────┐   │
│  │ DocumentController                                  │   │
│  │  - POST /api/v1/documents                           │   │
│  └──────────────┬──────────────────────────────────────┘   │
│                 │                                            │
│  ┌──────────────▼──────────────────┐                       │
│  │ DocumentRegistrationService     │                       │
│  └──────────────┬──────────────────┘                       │
│                 │                                            │
│  ┌──────────────▼──────────────────┐                       │
│  │ DocumentRepository (JPA)        │                       │
│  └──────────────┬──────────────────┘                       │
│                 │                                            │
└─────────────────┼────────────────────────────────────────────┘
                  │
                  ▼
         ┌────────────────┐
         │   PostgreSQL   │
         │   (Documents)  │
         └────────────────┘
```

---

## 🎯 Padrões de Comunicação

### 1. Client HTTP Declarativo (Imperativo)

```java
@HttpExchange(url = "/api/v1/documents")
public interface DocumentServiceClient {
    
    @PostExchange
    DocumentResponse createDocument(@RequestBody DocumentRequest request);
}
```

**Configuração:**
```java
@Bean
public DocumentServiceClient documentServiceClient() {
    RestClient restClient = RestClient.builder()
            .baseUrl(documentServiceUrl)
            .build();

    HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build();

    return factory.createClient(DocumentServiceClient.class);
}
```

### 2. Service Orchestration

```java
public DocumentResponseDto createDocumentForUser(UUID userId, String title, String content) {
    // 1. Valida usuário existe
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // 2. Prepara request para Document Service
    DocumentRequest req = new DocumentRequest(title, content, userId);

    // 3. Chama Document Service via RestClient
    DocumentResponse response = documentServiceClient.createDocument(req);

    // 4. Mapeia e retorna
    return new DocumentResponseDto(/* ... */);
}
```

---

## 📚 Recursos Adicionais

### Documentação dos Serviços

- **User Service**: `http://localhost:8081/actuator/health`
- **Document Service**: `http://localhost:8082/actuator/health`
- **Eureka Dashboard**: `http://localhost:8761`

### Tecnologias Utilizadas

- **Spring Boot**: 3.5.6
- **Java**: 21
- **Spring Cloud**: 2025.0.0
- **PostgreSQL**: 16
- **Spring Data JPA**: Persistência
- **Spring Boot Actuator**: Observabilidade
- **Lombok**: Redução de boilerplate
- **Jakarta Validation**: Validação de dados

---

## 📞 Suporte

Para problemas ou dúvidas:

1. Verifique os logs dos serviços
2. Confirme que todos os serviços estão rodando
3. Valide as variáveis de ambiente no Postman
4. Consulte os exemplos de response incluídos na collection

---

## 📄 Licença

Este projeto faz parte do sistema DocChain desenvolvido para demonstração de arquitetura de microsserviços Spring Boot moderna.

---

**Versão:** 1.0.0  
**Data:** Outubro 2025  
**Autor:** DocChain Team
