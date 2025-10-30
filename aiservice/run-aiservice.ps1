# Script para rodar o AI Service com a API Key configurada

# Configura o Java 21
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Define a variável de ambiente da API Key
$env:OPENAI_API_KEY = ""

Write-Host "☕ Java configurado: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host "✅ Variável OPENAI_API_KEY configurada" -ForegroundColor Green
Write-Host "🚀 Iniciando AI Service..." -ForegroundColor Cyan

# Navega para o diretório do projeto
Set-Location $PSScriptRoot

# Executa o serviço
./mvnw spring-boot:run
