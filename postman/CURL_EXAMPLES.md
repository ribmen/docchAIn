# 🔧 DocChain - cURL Examples

Exemplos de chamadas cURL para testar os microsserviços sem Postman.

## 📋 Índice

- [User Service](#user-service)
- [Document Service](#document-service)
- [Integration Flow](#integration-flow)
- [Health Checks](#health-checks)

---

## 🧑 User Service

### Criar Usuário

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }'
```

**Response Esperado (201 Created):**
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

**Salvar o ID do usuário:**
```bash
# Linux/Mac
export USER_ID="550e8400-e29b-41d4-a716-446655440000"

# Windows PowerShell
$USER_ID="550e8400-e29b-41d4-a716-446655440000"

# Windows CMD
set USER_ID=550e8400-e29b-41d4-a716-446655440000
```

---

### Criar Documento para Usuário

```bash
curl -X POST "http://localhost:8081/users/${USER_ID}/documents" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Contrato de Prestação de Serviços",
    "content": "Este contrato estabelece os termos e condições para prestação de serviços..."
  }'
```

**Response Esperado (201 Created):**
```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "title": "Contrato de Prestação de Serviços",
  "content": "Este contrato estabelece os termos e condições para prestação de serviços...",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED",
  "createdAt": "2025-10-18T15:45:30+00:00"
}
```

---

## 📄 Document Service

### Criar Documento (Direto)

```bash
curl -X POST http://localhost:8082/api/v1/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Manual do Usuário",
    "content": "Este manual contém instruções detalhadas sobre como utilizar o sistema...",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response Esperado (201 Created):**
```json
{
  "id": "9b2c3f4a-1234-5678-90ab-cdef12345678",
  "fileName": null,
  "title": "Manual do Usuário",
  "content": "Este manual contém instruções detalhadas sobre como utilizar o sistema...",
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

## 🔄 Integration Flow

### Fluxo Completo com Captura de IDs

**Linux/Mac/Git Bash:**

```bash
#!/bin/bash

# 1. Criar usuário e capturar ID
echo "📝 Criando usuário..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Carlos Eduardo",
    "email": "carlos.eduardo@docchain.com"
  }')

# Extrair user_id do JSON (requer jq)
USER_ID=$(echo $USER_RESPONSE | jq -r '.id')
echo "✅ Usuário criado com ID: $USER_ID"

# 2. Criar documento para o usuário
echo "📄 Criando documento para o usuário..."
DOC_RESPONSE=$(curl -s -X POST "http://localhost:8081/users/${USER_ID}/documents" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Documento de Integração - Teste Completo",
    "content": "Este documento foi criado através do fluxo completo de integração."
  }')

# Extrair document_id
DOC_ID=$(echo $DOC_RESPONSE | jq -r '.id')
echo "✅ Documento criado com ID: $DOC_ID"

echo ""
echo "🎉 Fluxo completo executado com sucesso!"
echo "   User ID: $USER_ID"
echo "   Document ID: $DOC_ID"
```

**Windows PowerShell:**

```powershell
# 1. Criar usuário
Write-Host "📝 Criando usuário..." -ForegroundColor Cyan
$userResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/users" `
  -ContentType "application/json" `
  -Body (@{
    fullName = "Carlos Eduardo"
    email = "carlos.eduardo@docchain.com"
  } | ConvertTo-Json)

$userId = $userResponse.id
Write-Host "✅ Usuário criado com ID: $userId" -ForegroundColor Green

# 2. Criar documento
Write-Host "📄 Criando documento para o usuário..." -ForegroundColor Cyan
$docResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/users/$userId/documents" `
  -ContentType "application/json" `
  -Body (@{
    title = "Documento de Integração - Teste Completo"
    content = "Este documento foi criado através do fluxo completo de integração."
  } | ConvertTo-Json)

$docId = $docResponse.id
Write-Host "✅ Documento criado com ID: $docId" -ForegroundColor Green

Write-Host ""
Write-Host "🎉 Fluxo completo executado com sucesso!" -ForegroundColor Yellow
Write-Host "   User ID: $userId"
Write-Host "   Document ID: $docId"
```

---

## 🏥 Health Checks

### User Service Health

```bash
curl -X GET http://localhost:8081/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

---

### Document Service Health

```bash
curl -X GET http://localhost:8082/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

---

### Eureka Discovery

```bash
curl -X GET http://localhost:8761
```

Retorna página HTML com dashboard do Eureka.

---

## 🧪 Testes de Validação

### Erro: Campo Obrigatório Faltando

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": ""
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2025-10-18T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "fullName",
      "message": "must not be blank"
    },
    {
      "field": "email",
      "message": "must not be blank"
    }
  ]
}
```

---

### Erro: Usuário Não Encontrado

```bash
curl -X POST http://localhost:8081/users/99999999-9999-9999-9999-999999999999/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test",
    "content": "Test content"
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2025-10-18T15:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "User not found with id: 99999999-9999-9999-9999-999999999999"
}
```

---

### Erro: Owner ID Obrigatório

```bash
curl -X POST http://localhost:8082/api/v1/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Document",
    "content": "Some content"
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2025-10-18T16:25:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Owner ID must be set to create a document."
}
```

---

## 🔍 Testes com Pretty Print

### Com `jq` (JSON formatter):

```bash
# User Service
curl -s -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }' | jq

# Document Service
curl -s -X POST http://localhost:8082/api/v1/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test",
    "content": "Content",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000"
  }' | jq
```

### Com `python` (se `jq` não estiver disponível):

```bash
curl -s -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }' | python -m json.tool
```

---

## 📊 Testes de Performance

### Apache Bench (ab)

```bash
# Teste de carga - 100 requisições, 10 concorrentes
ab -n 100 -c 10 -T 'application/json' \
  -p user-payload.json \
  http://localhost:8081/users
```

**user-payload.json:**
```json
{
  "fullName": "Load Test User",
  "email": "loadtest@example.com"
}
```

---

### Vegeta (Load Testing)

```bash
# Instalar vegeta
go install github.com/tsenart/vegeta@latest

# Criar arquivo de targets
echo "POST http://localhost:8081/users
Content-Type: application/json
@user-payload.json
" > targets.txt

# Executar teste: 50 req/s por 10 segundos
vegeta attack -targets=targets.txt -rate=50 -duration=10s | vegeta report
```

---

## 🛠️ Debugging com Verbose

### Ver Headers e Body Completo

```bash
curl -v -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }'
```

**Output inclui:**
- Request headers
- Response headers
- Status code
- Response body

---

### Salvar Response em Arquivo

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }' -o user-response.json
```

---

### Apenas HTTP Status Code

```bash
curl -s -o /dev/null -w "%{http_code}" \
  -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "João Silva",
    "email": "joao.silva@example.com"
  }'
```

---

## 🎯 Scripts de Teste Completo

### Bash Script (Linux/Mac/Git Bash)

**test-docchain.sh:**
```bash
#!/bin/bash

echo "🧪 Testando DocChain Microservices"
echo "===================================="
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para testar endpoint
test_endpoint() {
  local name=$1
  local url=$2
  local expected_status=$3
  
  echo -n "Testing $name... "
  status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
  
  if [ "$status" = "$expected_status" ]; then
    echo -e "${GREEN}✓ PASSED${NC} (HTTP $status)"
    return 0
  else
    echo -e "${RED}✗ FAILED${NC} (Expected $expected_status, got $status)"
    return 1
  fi
}

# Health Checks
echo "🏥 Health Checks:"
test_endpoint "User Service Health" "http://localhost:8081/actuator/health" "200"
test_endpoint "Document Service Health" "http://localhost:8082/actuator/health" "200"
echo ""

# Criar usuário
echo "👤 Criando usuário..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com"
  }')

if echo "$USER_RESPONSE" | jq -e '.id' > /dev/null 2>&1; then
  USER_ID=$(echo $USER_RESPONSE | jq -r '.id')
  echo -e "${GREEN}✓ Usuário criado${NC}: $USER_ID"
else
  echo -e "${RED}✗ Falha ao criar usuário${NC}"
  exit 1
fi
echo ""

# Criar documento
echo "📄 Criando documento..."
DOC_RESPONSE=$(curl -s -X POST "http://localhost:8081/users/${USER_ID}/documents" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Document",
    "content": "Test content"
  }')

if echo "$DOC_RESPONSE" | jq -e '.id' > /dev/null 2>&1; then
  DOC_ID=$(echo $DOC_RESPONSE | jq -r '.id')
  echo -e "${GREEN}✓ Documento criado${NC}: $DOC_ID"
else
  echo -e "${RED}✗ Falha ao criar documento${NC}"
  exit 1
fi
echo ""

echo -e "${GREEN}🎉 Todos os testes passaram!${NC}"
```

**Executar:**
```bash
chmod +x test-docchain.sh
./test-docchain.sh
```

---

### PowerShell Script (Windows)

**Test-DocChain.ps1:**
```powershell
Write-Host "🧪 Testando DocChain Microservices" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

function Test-Endpoint {
    param($Name, $Url, $ExpectedStatus)
    
    Write-Host "Testing $Name... " -NoNewline
    try {
        $response = Invoke-WebRequest -Uri $Url -Method Get -SkipHttpErrorCheck
        if ($response.StatusCode -eq $ExpectedStatus) {
            Write-Host "✓ PASSED" -ForegroundColor Green -NoNewline
            Write-Host " (HTTP $($response.StatusCode))"
            return $true
        } else {
            Write-Host "✗ FAILED" -ForegroundColor Red -NoNewline
            Write-Host " (Expected $ExpectedStatus, got $($response.StatusCode))"
            return $false
        }
    } catch {
        Write-Host "✗ FAILED" -ForegroundColor Red -NoNewline
        Write-Host " (Error: $_)"
        return $false
    }
}

# Health Checks
Write-Host "🏥 Health Checks:"
Test-Endpoint "User Service Health" "http://localhost:8081/actuator/health" 200
Test-Endpoint "Document Service Health" "http://localhost:8082/actuator/health" 200
Write-Host ""

# Criar usuário
Write-Host "👤 Criando usuário..."
try {
    $userResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/users" `
        -ContentType "application/json" `
        -Body (@{
            fullName = "Test User"
            email = "test@example.com"
        } | ConvertTo-Json)
    
    $userId = $userResponse.id
    Write-Host "✓ Usuário criado: $userId" -ForegroundColor Green
} catch {
    Write-Host "✗ Falha ao criar usuário: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Criar documento
Write-Host "📄 Criando documento..."
try {
    $docResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/users/$userId/documents" `
        -ContentType "application/json" `
        -Body (@{
            title = "Test Document"
            content = "Test content"
        } | ConvertTo-Json)
    
    $docId = $docResponse.id
    Write-Host "✓ Documento criado: $docId" -ForegroundColor Green
} catch {
    Write-Host "✗ Falha ao criar documento: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

Write-Host "🎉 Todos os testes passaram!" -ForegroundColor Green
```

**Executar:**
```powershell
.\Test-DocChain.ps1
```

---

## 📚 Referências

- **cURL Documentation**: https://curl.se/docs/
- **jq Manual**: https://jqlang.github.io/jq/manual/
- **Apache Bench**: https://httpd.apache.org/docs/2.4/programs/ab.html
- **Vegeta**: https://github.com/tsenart/vegeta

---

**Versão:** 1.0.0  
**Data:** Outubro 2025
