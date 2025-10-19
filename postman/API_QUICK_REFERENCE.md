# 🎯 DocChain API - Quick Reference

## 📍 Endpoints Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    USER SERVICE (Port 8081)                      │
└─────────────────────────────────────────────────────────────────┘

POST /users
├─ Request:  { fullName, email }
└─ Response: { id, fullName, email, createdAt, documents[] }

POST /users/{userId}/documents
├─ Request:  { title, content }
└─ Response: { id, title, content, ownerId, status, createdAt }

GET /actuator/health
└─ Response: { status: "UP" }


┌─────────────────────────────────────────────────────────────────┐
│                  DOCUMENT SERVICE (Port 8082)                    │
└─────────────────────────────────────────────────────────────────┘

POST /api/v1/documents
├─ Request:  { title, content, ownerId }
└─ Response: { id, title, content, ownerId, status, ... }

GET /actuator/health
└─ Response: { status: "UP" }
```

---

## 🔄 Communication Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │
     │ 1. POST /users/{id}/documents
     │    { title, content }
     ▼
┌─────────────────────────────┐
│   User Service (8081)       │
│                             │
│  ┌─────────────────────┐   │
│  │ UserController      │   │
│  └──────────┬──────────┘   │
│             │               │
│  ┌──────────▼──────────┐   │
│  │ UserDocumentService │   │
│  │  - Valida user      │   │
│  │  - Prepara request  │   │
│  └──────────┬──────────┘   │
│             │               │
│  ┌──────────▼──────────┐   │
│  │ DocumentClient      │   │
│  │  (RestClient)       │   │
│  └──────────┬──────────┘   │
└─────────────┼───────────────┘
              │
              │ 2. HTTP POST /api/v1/documents
              │    { title, content, ownerId }
              ▼
┌─────────────────────────────────┐
│   Document Service (8082)       │
│                                 │
│  ┌──────────────────────────┐  │
│  │ DocumentController       │  │
│  └──────────┬───────────────┘  │
│             │                   │
│  ┌──────────▼──────────────┐   │
│  │ DocumentRegistration    │   │
│  │ Service                 │   │
│  └──────────┬──────────────┘   │
│             │                   │
│  ┌──────────▼──────────────┐   │
│  │ DocumentRepository      │   │
│  │    (JPA)                │   │
│  └──────────┬──────────────┘   │
└─────────────┼───────────────────┘
              │
              ▼
      ┌───────────────┐
      │  PostgreSQL   │
      │  (Documents)  │
      └───────────────┘
              │
              │ 3. Returns Document entity
              │
              ▼
      Back to User Service
              │
              │ 4. Maps to DocumentResponseDto
              │
              ▼
      Returns to Client
```

---

## 📦 Request/Response Examples

### 🧑 Create User

```bash
POST http://localhost:8081/users
```

**Request:**
```json
{
  "fullName": "João Silva",
  "email": "joao.silva@example.com"
}
```

**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "fullName": "João Silva",
  "email": {
    "value": "joao.silva@example.com"
  },
  "createdAt": "2025-10-18T14:30:00",
  "documents": []
}
```

---

### 📄 Create Document for User

```bash
POST http://localhost:8081/users/{userId}/documents
```

**Request:**
```json
{
  "title": "Contrato de Serviços",
  "content": "Este contrato estabelece..."
}
```

**Response (201):**
```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "title": "Contrato de Serviços",
  "content": "Este contrato estabelece...",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED",
  "createdAt": "2025-10-18T15:45:30+00:00"
}
```

---

### 📝 Create Document (Direct)

```bash
POST http://localhost:8082/api/v1/documents
```

**Request:**
```json
{
  "title": "Manual do Usuário",
  "content": "Instruções detalhadas...",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (201):**
```json
{
  "id": "9b2c3f4a-1234-5678-90ab-cdef12345678",
  "fileName": null,
  "title": "Manual do Usuário",
  "content": "Instruções detalhadas...",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CREATED",
  "createdAt": "2025-10-18T16:20:45+00:00",
  "version": 0
}
```

---

## ⚡ Quick Commands

### Postman
```
1. Import → docchain-collection.postman_collection.json
2. Import → docchain-environment.postman_environment.json
3. Select "DocChain - Local Development" environment
4. Run: User Service → Create User
```

### cURL
```bash
# Create user
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"João Silva","email":"joao.silva@example.com"}'

# Create document (replace USER_ID)
curl -X POST http://localhost:8081/users/USER_ID/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"My Doc","content":"Content..."}'
```

### Health Checks
```bash
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Document Service
curl http://localhost:8761                   # Eureka
```

---

## 🎨 Postman Collection Structure

```
📁 DocChain Microservices Collection
│
├── 📂 User Service
│   ├── Create User                           ✅ Auto-save user_id
│   └── Create Document for User              ✅ Auto-save document_id
│
├── 📂 Document Service
│   └── Create Document (Direct)
│
├── 📂 Integration Examples
│   ├── Full Flow - Create User and Document
│   │   ├── Step 1 - Create User             🔄 Saves integration_user_id
│   │   └── Step 2 - Create Document         🔄 Uses integration_user_id
│   └── Microservices Communication Test      🧪 Tests User → Document
│
└── 📂 Health Checks
    ├── User Service Health
    ├── Document Service Health
    └── Eureka Discovery Status
```

---

## 🔧 Environment Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `user_service_url` | `http://localhost:8081` | User Service base URL |
| `document_service_url` | `http://localhost:8082` | Document Service base URL |
| `eureka_url` | `http://localhost:8761` | Eureka Discovery URL |
| `user_id` | (auto) | Saved after creating user |
| `document_id` | (auto) | Saved after creating document |
| `integration_user_id` | (auto) | Used in integration flow |

---

## ✅ Testing Checklist

### Setup
- [ ] PostgreSQL running (ports 5432, 5433)
- [ ] Discovery Service running (port 8761)
- [ ] Document Service running (port 8082)
- [ ] User Service running (port 8081)
- [ ] Postman collection imported
- [ ] Environment selected

### Basic Tests
- [ ] User Service health check passes
- [ ] Document Service health check passes
- [ ] Create user succeeds
- [ ] Create document for user succeeds

### Integration Tests
- [ ] User → Document communication works
- [ ] Error handling works (invalid user)
- [ ] Validation works (missing fields)
- [ ] Response mapping is correct

---

## 🐛 Common Errors

### ❌ Connection Refused
```
Cause: Service not running
Fix:   cd service && ./mvnw spring-boot:run
```

### ❌ User not found
```
Cause: Invalid user_id
Fix:   Create user first, use returned ID
```

### ❌ Owner ID must be set
```
Cause: Missing ownerId in direct document creation
Fix:   Add "ownerId": "uuid" to request body
```

### ❌ Validation failed
```
Cause: Missing required fields
Fix:   Ensure all required fields are present:
       - fullName (user)
       - email (user)
       - title (document)
       - content (document)
```

---

## 📊 HTTP Status Codes

| Code | Meaning | When |
|------|---------|------|
| 200 | OK | Health checks |
| 201 | Created | User/Document created successfully |
| 400 | Bad Request | Validation error, missing fields |
| 404 | Not Found | User/Document doesn't exist |
| 500 | Server Error | Internal server error |

---

## 🚀 Performance Tips

### Batch Testing
```bash
# Run integration flow in Postman
Collection → Integration Examples → Run
```

### Parallel Testing
```bash
# Use Postman Collection Runner
Collection → Run → Set iterations
```

### Load Testing
```bash
# Apache Bench
ab -n 100 -c 10 -T 'application/json' \
   -p payload.json http://localhost:8081/users

# Vegeta
vegeta attack -targets=targets.txt -rate=50 -duration=10s
```

---

## 📚 Documentation Links

| Document | Description |
|----------|-------------|
| **README_API_TESTS.md** | Complete overview and getting started |
| **POSTMAN_GUIDE.md** | Detailed Postman guide with examples |
| **CURL_EXAMPLES.md** | cURL commands and scripts |
| **This file** | Quick reference cheat sheet |

---

## 💡 Pro Tips

1. **Auto-save IDs**: Tests automatically save user_id and document_id
2. **Console Logs**: Open Postman Console (View → Show Postman Console)
3. **Examples**: Each request has success/error examples
4. **Variables**: Use {{variable}} syntax in URLs and bodies
5. **Scripts**: Pre-request and test scripts automate workflows
6. **Runner**: Use Collection Runner for batch testing
7. **Environments**: Switch between dev/staging/prod easily

---

## 🎯 Next Steps

1. ✅ Import Postman collection
2. ✅ Start all services
3. ✅ Run health checks
4. ✅ Create test user
5. ✅ Create test document
6. ✅ Run integration tests
7. ✅ Explore other endpoints

---

**Quick Access:**
- 📖 [Full Postman Guide](../POSTMAN_GUIDE.md)
- 🔧 [cURL Examples](CURL_EXAMPLES.md)
- 📦 [Main README](../README_API_TESTS.md)

---

**Happy Testing! 🚀**
