# 📮 DocChain API Testing Suite

> **Suite completa de testes de API para os microsserviços User e Document**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Postman](https://img.shields.io/badge/Postman-Ready-orange.svg)](https://www.postman.com/)
[![Documentation](https://img.shields.io/badge/Docs-Complete-green.svg)](#)

---

## 🎯 Quick Start

**1. Abrir overview visual:**
```
📄 START_HERE.txt
```

**2. Importar no Postman:**
```
Import → docchain-collection.postman_collection.json
Import → docchain-environment.postman_environment.json
```

**3. Testar:**
```
User Service → Create User → Send
```

**🎉 Pronto! Você tem uma suite completa de testes funcionando.**

---

## 📦 O Que Foi Gerado

### Arquivos Postman
- ✅ `docchain-collection.postman_collection.json` - Collection completa
- ✅ `docchain-environment.postman_environment.json` - Environment pré-configurado

### Documentação (8 arquivos, ~165 páginas)
- ✅ `SUMARIO_EXECUTIVO.md` - **[COMECE AQUI]** Resumo executivo
- ✅ `START_HERE.txt` - Overview visual em ASCII
- ✅ `README_API_TESTS.md` - Documentação principal completa
- ✅ `POSTMAN_GUIDE.md` - Guia completo do Postman
- ✅ `CURL_EXAMPLES.md` - Exemplos cURL e scripts
- ✅ `API_QUICK_REFERENCE.md` - Cheat sheet rápido
- ✅ `DOCUMENTATION_INDEX.md` - Índice de navegação
- ✅ Este README - Ponto de entrada

---

## 🗺️ Guia de Navegação

### 👤 Você é...

**🆕 Novo no projeto?**
→ Leia [`SUMARIO_EXECUTIVO.md`](SUMARIO_EXECUTIVO.md) (2 min)  
→ Abra [`START_HERE.txt`](START_HERE.txt) (visual overview)  
→ Importe a collection no Postman  

**👨‍💻 Desenvolvedor?**
→ Use [`API_QUICK_REFERENCE.md`](./API_QUICK_REFERENCE.md) como cheat sheet  
→ Consulte [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) para exemplos detalhados  
→ Teste durante o desenvolvimento  

**🧪 QA/Tester?**
→ Leia [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) completo  
→ Execute fluxos de integração  
→ Use casos de teste pré-configurados  

**🔧 DevOps/SRE?**
→ Use [`CURL_EXAMPLES.md`](./CURL_EXAMPLES.md) para automação  
→ Implemente scripts em CI/CD  
→ Configure health checks  

**❓ Perdido?**
→ Abra [`DOCUMENTATION_INDEX.md`](DOCUMENTATION_INDEX.md)  
→ Navegue por documento, papel ou tarefa  

---

## 📊 Estatísticas

| Item | Quantidade |
|------|-----------|
| Arquivos gerados | 10 |
| Páginas de documentação | ~165 |
| Endpoints documentados | 8 |
| Casos de teste | 15+ |
| Exemplos de request/response | 30+ |
| Scripts de automação | 6+ |
| Exemplos de código | 50+ |

---

## 🏗️ Arquitetura

```
Cliente (Postman/cURL)
    ↓
User Service (8081)
    ↓
RestClient (Imperativo)
    ↓
Document Service (8082)
    ↓
PostgreSQL
```

**Detalhes completos:** [`README_API_TESTS.md`](README_API_TESTS.md) → Seção Arquitetura

---

## 📍 Endpoints

### User Service (8081)
- `POST /users` - Criar usuário
- `POST /users/{id}/documents` - Criar documento para usuário
- `GET /actuator/health` - Health check

### Document Service (8082)
- `POST /api/v1/documents` - Criar documento
- `GET /actuator/health` - Health check

**Exemplos completos:** [`API_QUICK_REFERENCE.md`](./API_QUICK_REFERENCE.md)

---

## ✨ Features

✅ Collection completa do Postman com 8 endpoints  
✅ Testes automáticos em cada request  
✅ Auto-save de IDs em variáveis  
✅ Fluxos de integração pré-configurados  
✅ 30+ exemplos de request/response  
✅ Scripts Bash e PowerShell para automação  
✅ Documentação extensiva (~165 páginas)  
✅ Guias de troubleshooting  
✅ Health checks configurados  

---

## 🚀 Uso Rápido

### Postman
```
1. Import → docchain-collection.postman_collection.json
2. Import → docchain-environment.postman_environment.json
3. Select "DocChain - Local Development"
4. Run: User Service → Create User
```

### cURL
```bash
# Criar usuário
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"João Silva","email":"joao@example.com"}'

# Health check
curl http://localhost:8081/actuator/health
```

---

## 📖 Documentação Detalhada

| Documento | Propósito | Tempo |
|-----------|-----------|-------|
| [`SUMARIO_EXECUTIVO.md`](SUMARIO_EXECUTIVO.md) | Resumo executivo | 2 min |
| [`START_HERE.txt`](START_HERE.txt) | Overview visual | 2 min |
| [`README_API_TESTS.md`](README_API_TESTS.md) | Documentação principal | 10 min |
| [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) | Guia completo Postman | 20 min |
| [`CURL_EXAMPLES.md`](./CURL_EXAMPLES.md) | Exemplos cURL | 15 min |
| [`API_QUICK_REFERENCE.md`](./API_QUICK_REFERENCE.md) | Cheat sheet | 2 min |
| [`DOCUMENTATION_INDEX.md`](DOCUMENTATION_INDEX.md) | Índice navegação | 3 min |

**Total:** ~1 hora para ler toda a documentação

---

## 🎓 Recomendação de Leitura

### ⚡ Rápido (5 minutos)
1. [`SUMARIO_EXECUTIVO.md`](SUMARIO_EXECUTIVO.md)
2. [`API_QUICK_REFERENCE.md`](./API_QUICK_REFERENCE.md)
3. Importar collection
4. Testar

### 📚 Completo (30 minutos)
1. [`README_API_TESTS.md`](README_API_TESTS.md)
2. [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) - Seções principais
3. Executar fluxos de integração

### 🤖 Automação (1 hora)
1. [`CURL_EXAMPLES.md`](./CURL_EXAMPLES.md) - Completo
2. Scripts Bash/PowerShell
3. Implementar em CI/CD

---

## ✅ Checklist Pré-requisitos

Antes de começar:

- [ ] PostgreSQL rodando (portas 5432, 5433)
- [ ] Discovery Service rodando (porta 8761)
- [ ] Document Service rodando (porta 8082)
- [ ] User Service rodando (porta 8081)
- [ ] Postman instalado (ou cURL disponível)

**Verificar:**
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

---

## 🐛 Troubleshooting

### Serviço não responde
```bash
curl http://localhost:8081/actuator/health
```

### User not found
Criar usuário primeiro, verificar que `user_id` foi salvo nas variáveis.

### Mais detalhes
[`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) → Seção Troubleshooting

---

## 💡 Pro Tips

1. **Use o Console do Postman** - Logs detalhados de cada request
2. **IDs são auto-salvos** - Não copie manualmente
3. **Cada request tem testes** - Validação automática
4. **Use Collection Runner** - Testes em lote
5. **Consulte o Quick Reference** - Referência rápida sempre à mão

---

## 📞 Suporte

**Encontrar informação:**
→ [`DOCUMENTATION_INDEX.md`](DOCUMENTATION_INDEX.md) - Navegação completa

**Troubleshooting:**
→ [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) → Troubleshooting  
→ [`API_QUICK_REFERENCE.md`](./API_QUICK_REFERENCE.md) → Common Errors

**Exemplos:**
→ [`POSTMAN_GUIDE.md`](POSTMAN_GUIDE.md) → Exemplos de Uso  
→ [`CURL_EXAMPLES.md`](./CURL_EXAMPLES.md) → Comandos

---

## 🎯 Próximos Passos

1. ✅ Ler [`SUMARIO_EXECUTIVO.md`](SUMARIO_EXECUTIVO.md)
2. ✅ Abrir [`START_HERE.txt`](START_HERE.txt)
3. ✅ Importar collection no Postman
4. ✅ Executar primeiro teste
5. ✅ Explorar outros endpoints
6. ✅ Consultar [`DOCUMENTATION_INDEX.md`](DOCUMENTATION_INDEX.md) quando necessário

---

## 🌟 Destaques

### Tecnologias
- Spring Boot 3.5.6
- Java 21
- RestClient (Imperativo)
- PostgreSQL 16
- Spring Data JPA
- Eureka Discovery

### Padrões
- Arquitetura em camadas
- Comunicação HTTP declarativa
- DTOs tipados
- Testes automatizados
- Observabilidade com Actuator

---

## 📄 Licença

Este projeto faz parte do sistema DocChain desenvolvido para demonstração de arquitetura de microsserviços Spring Boot moderna.

---

## 🎉 Pronto!

**Você tem tudo que precisa para começar.**

**Próximo passo:** Abra [`SUMARIO_EXECUTIVO.md`](SUMARIO_EXECUTIVO.md) ou [`START_HERE.txt`](START_HERE.txt)

---

**Versão:** 1.0.0  
**Data:** Outubro 2025  
**Projeto:** DocChain Microservices  

**🚀 Happy Testing! 🚀**
