# 📮 DocChain API Testing Suite

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Postman](https://img.shields.io/badge/Postman-Ready-orange.svg)](https://www.postman.com/)

Suite completa de testes de API para os microsserviços **User Service** e **Document Service** do projeto DocChain.

---

## 📦 Arquivos Gerados

| Arquivo | Descrição | Uso |
|---------|-----------|-----|
| **`docchain-collection.postman_collection.json`** | Postman Collection completa | Importar no Postman |
| **`docchain-environment.postman_environment.json`** | Variáveis de ambiente | Importar no Postman |
| **`POSTMAN_GUIDE.md`** | Guia completo do Postman | Documentação detalhada |
| **`CURL_EXAMPLES.md`** | Exemplos de cURL | Testes via linha de comando |
| **`README_API_TESTS.md`** | Este arquivo | Visão geral |

---

## 🚀 Quick Start

### Opção 1: Postman (Recomendado)

1. **Importar Collection:**
   ```
   Postman → Import → docchain-collection.postman_collection.json
   ```

2. **Importar Environment:**
   ```
   Postman → Import → docchain-environment.postman_environment.json
   ```

3. **Selecionar Environment:**
   ```
   Dropdown superior direito → "DocChain - Local Development"
   ```

4. **Executar:**
   ```
   User Service → Create User → Send
   ```

**📖 Documentação completa:** [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)

---

### Opção 2: cURL

```bash
# 1. Criar usuário
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }'

# 2. Criar documento (substitua {USER_ID} pelo ID retornado)
curl -X POST http://localhost:8081/users/{USER_ID}/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Meu Documento",
    "content": "Conteúdo do documento..."
  }'
```

**📖 Mais exemplos:** [CURL_EXAMPLES.md](./CURL_EXAMPLES.md)

---

## 🏗️ Arquitetura

```
┌─────────────┐
│   Client    │
│  (Postman)  │
└──────┬──────┘
       │
       │ HTTP/REST
       │
┌──────▼───────────────────┐       ┌──────────────────────────┐
│   User Service (8081)    │──────→│ Document Service (8082)  │
│                          │       │                          │
│  - Create User           │       │  - Create Document       │
│  - Create Doc for User   │       │  - Manage Documents      │
│                          │       │                          │
│  RestClient (Imperativo) │       │  Spring MVC              │
└──────────────────────────┘       └───────────┬──────────────┘
       │                                       │
       │ PostgreSQL                            │ PostgreSQL
       ▼                                       ▼
┌──────────────┐                      ┌──────────────┐
│ docchain_    │                      │ docchain_    │
│   users      │                      │  documents   │
└──────────────┘                      └──────────────┘
```

---

## 📋 Endpoints Disponíveis

### User Service (Port 8081)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/users` | Criar novo usuário |
| `POST` | `/users/{userId}/documents` | Criar documento para usuário |
| `GET` | `/actuator/health` | Health check |

### Document Service (Port 8082)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/documents` | Criar documento (direto) |
| `GET` | `/actuator/health` | Health check |

---

## 📊 Collection Features

### ✅ Testes Automáticos

Cada request inclui testes automáticos:

```javascript
✓ User created successfully
✓ Response contains user ID
✓ Document created for user
✓ Microservices communication successful
✓ Response time is acceptable
```

### 🔄 Variáveis Automáticas

IDs são salvos automaticamente:

- `user_id` - Após criar usuário
- `document_id` - Após criar documento
- `integration_user_id` - Para fluxo de integração

### 📝 Scripts Inteligentes

- **Pre-request Scripts**: Validam variáveis necessárias
- **Test Scripts**: Automatizam validações e salvam IDs
- **Console Logs**: Logs detalhados de execução

### 📚 Exemplos de Response

Cada endpoint inclui exemplos de:
- ✅ Success responses (201, 200)
- ❌ Error responses (400, 404, 500)

---

## 🧪 Fluxos de Teste

### 1. Fluxo Básico

```
1. Create User
   └─→ Salva user_id
2. Create Document for User
   └─→ Usa user_id do passo 1
   └─→ Salva document_id
```

### 2. Fluxo de Integração Completo

```
Integration Examples/
  └─→ Full Flow - Create User and Document/
      ├─→ Step 1 - Create User
      │   └─→ Salva integration_user_id
      └─→ Step 2 - Create Document via User Service
          └─→ Usa integration_user_id
          └─→ User Service → Document Service
```

### 3. Teste de Comunicação entre Microsserviços

```
Integration Examples/
  └─→ Microservices Communication Test
      └─→ Valida comunicação User → Document
      └─→ Testa RestClient
      └─→ Verifica mapeamento de DTOs
```

---

## 🔧 Configuração

### Pré-requisitos

1. **Java 21** instalado
2. **Maven** instalado
3. **PostgreSQL** rodando
4. **Postman** instalado (opcional)

### Iniciar Serviços

```bash
# 1. PostgreSQL via Docker
docker-compose up -d postgres-user postgres-document

# 2. Eureka Discovery
cd discovery-service
./mvnw spring-boot:run

# 3. Document Service
cd ../document
./mvnw spring-boot:run

# 4. User Service
cd ../user
./mvnw spring-boot:run
```

### Verificar Serviços

```bash
# User Service
curl http://localhost:8081/actuator/health

# Document Service
curl http://localhost:8082/actuator/health

# Eureka Dashboard
open http://localhost:8761
```

---

## 🎯 Casos de Uso

### 1. Desenvolvimento Local

Use a collection do Postman para:
- Testar novos endpoints durante desenvolvimento
- Validar contratos de API
- Debugar comunicação entre serviços
- Testar validações e tratamento de erros

### 2. Testes de Integração

Use os fluxos de integração para:
- Validar comunicação User → Document
- Testar serialização/deserialização
- Verificar propagação de erros
- Validar consistência de dados

### 3. CI/CD

Use os scripts cURL para:
- Testes automatizados em pipeline
- Smoke tests em ambientes
- Validação pós-deploy
- Monitoramento de APIs

### 4. Documentação

Use a collection como:
- Documentação viva da API
- Exemplos para novos desenvolvedores
- Referência de contratos
- Base de conhecimento do projeto

---

## 📖 Documentação Completa

### Postman
- **[POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)** - Guia completo do Postman
  - Como importar
  - Estrutura da collection
  - Variáveis de ambiente
  - Testes automáticos
  - Exemplos de uso
  - Troubleshooting

### cURL
- **[CURL_EXAMPLES.md](./CURL_EXAMPLES.md)** - Exemplos de cURL
  - Comandos para todos os endpoints
  - Scripts Bash e PowerShell
  - Testes de carga
  - Debugging
  - Exemplos de erro

---

## 🐛 Troubleshooting

### Serviço não responde

```bash
# Verificar se está rodando
curl http://localhost:8081/actuator/health

# Ver logs
cd user
./mvnw spring-boot:run
```

### User not found

```bash
# 1. Criar usuário primeiro
curl -X POST http://localhost:8081/users ...

# 2. Copiar o 'id' do response
# 3. Usar o ID na próxima requisição
```

### PostgreSQL não conecta

```bash
# Verificar containers
docker ps

# Iniciar PostgreSQL
docker-compose up -d postgres-user postgres-document

# Verificar logs
docker logs docchain-postgres-user
```

### Variável não encontrada no Postman

1. Verifique se o environment está selecionado
2. Execute primeiro o request que seta a variável
3. Verifique no ícone 👁️ (olho) se a variável existe

---

## 🔍 Recursos Adicionais

### Scripts de Teste Automatizado

**Bash (Linux/Mac):**
```bash
# Ver CURL_EXAMPLES.md seção "Scripts de Teste Completo"
chmod +x test-docchain.sh
./test-docchain.sh
```

**PowerShell (Windows):**
```powershell
# Ver CURL_EXAMPLES.md seção "Scripts de Teste Completo"
.\Test-DocChain.ps1
```

### Testes de Carga

```bash
# Apache Bench
ab -n 100 -c 10 -T 'application/json' \
  -p user-payload.json \
  http://localhost:8081/users

# Vegeta
vegeta attack -targets=targets.txt -rate=50 -duration=10s | vegeta report
```

### Monitoramento

```bash
# Métricas Prometheus
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus

# Health checks
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

---

## 📊 Estrutura da Collection

```
📁 DocChain Microservices Collection
│
├── 📂 User Service (Port 8081)
│   ├── 📄 Create User
│   │   ├── ✅ Success example
│   │   ├── ❌ Validation error example
│   │   └── 🧪 Auto-save user_id
│   │
│   └── 📄 Create Document for User
│       ├── ✅ Success example
│       ├── ❌ User not found example
│       ├── ❌ Validation error example
│       └── 🧪 Auto-save document_id
│
├── 📂 Document Service (Port 8082)
│   └── 📄 Create Document (Direct)
│       ├── ✅ Success example
│       ├── ❌ Missing owner ID example
│       └── 🧪 Auto-save document_id
│
├── 📂 Integration Examples
│   ├── 📂 Full Flow - Create User and Document
│   │   ├── 📄 Step 1 - Create User
│   │   └── 📄 Step 2 - Create Document via User Service
│   │
│   └── 📄 Microservices Communication Test
│       ├── 🧪 Test User Service accessibility
│       ├── 🧪 Test Document Service accessibility
│       ├── 🧪 Test microservices communication
│       └── 📊 Console logs with detailed flow
│
└── 📂 Health Checks
    ├── 📄 User Service Health
    ├── 📄 Document Service Health
    └── 📄 Eureka Discovery Status
```

---

## 🎨 Features Destacadas

### 🚀 Auto-save de IDs
Todos os IDs retornados são automaticamente salvos em variáveis de ambiente.

### ✅ Testes Automáticos
Cada request valida automaticamente a resposta.

### 📝 Logs Detalhados
Console do Postman mostra fluxo completo de execução.

### 🔄 Exemplos de Response
Cada endpoint inclui exemplos de sucesso e erro.

### 🎯 Fluxos de Integração
Testes pré-configurados para validar comunicação entre serviços.

### 📊 Scripts Inteligentes
Pre-request e test scripts automatizam todo o fluxo.

---

## 🤝 Contribuindo

Para adicionar novos endpoints à collection:

1. Adicione o request no grupo apropriado
2. Configure scripts de teste
3. Adicione exemplos de response
4. Documente no POSTMAN_GUIDE.md
5. Adicione exemplo cURL no CURL_EXAMPLES.md

---

## 📝 Changelog

### v1.0.0 - Outubro 2025
- ✅ Collection inicial completa
- ✅ User Service endpoints
- ✅ Document Service endpoints
- ✅ Fluxos de integração
- ✅ Testes automáticos
- ✅ Exemplos de cURL
- ✅ Scripts de teste automatizado
- ✅ Health checks
- ✅ Documentação completa

---

## 📞 Suporte

### Documentação
- 📖 [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md) - Guia do Postman
- 📖 [CURL_EXAMPLES.md](CURL_EXAMPLES.md) - Exemplos cURL

### Problemas Comuns
1. Verificar se todos os serviços estão rodando
2. Confirmar que as portas estão corretas (8081, 8082)
3. Validar que o PostgreSQL está acessível
4. Verificar variáveis de ambiente no Postman

### Logs dos Serviços
```bash
# User Service
cd user && ./mvnw spring-boot:run

# Document Service
cd document && ./mvnw spring-boot:run
```

---

## 📄 Licença

Este projeto faz parte do sistema DocChain desenvolvido para demonstração de arquitetura de microsserviços Spring Boot moderna.

---

## 🌟 Recursos Utilizados

- **Spring Boot 3.5.6** - Framework principal
- **Java 21** - Linguagem de programação
- **PostgreSQL 16** - Banco de dados
- **RestClient** - Comunicação HTTP imperativa
- **Spring Data JPA** - Persistência
- **Spring Boot Actuator** - Observabilidade
- **Eureka Discovery** - Service discovery
- **Lombok** - Redução de boilerplate
- **Jakarta Validation** - Validação de dados

---

**Versão:** 1.0.0  
**Data:** Outubro 2025  
**Autor:** DocChain Team

**⭐ Happy Testing! ⭐**
