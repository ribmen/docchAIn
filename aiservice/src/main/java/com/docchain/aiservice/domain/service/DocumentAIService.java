package com.docchain.aiservice.domain.service;

import com.docchain.aiservice.api.model.request.OpenAiMessage;
import com.docchain.aiservice.api.model.request.OpenAiRequest;
import com.docchain.aiservice.api.model.response.GeneratedDocumentResponse;
import com.docchain.aiservice.api.model.response.ImprovedDocumentResponse;
import com.docchain.aiservice.api.model.response.MarkdownDocumentResponse;
import com.docchain.aiservice.api.model.response.OpenAiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DocumentAIService {

    private final RestClient restClient;
    private final String openAiKey;

    public DocumentAIService(@Value("${spring.ai.openai.api-key}") String openAiKey) {
        this.openAiKey = openAiKey;

        if (openAiKey == null || openAiKey.equals("default-key-if-not-set")) {
            throw new IllegalStateException("OpenAI API key not configured. Please set OPENAI_API_KEY environment variable.");
        }

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + openAiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public GeneratedDocumentResponse generateDocumentText(String prompt, String additionalContext) {
        log.info("Generating document from prompt: {}", prompt);

        String systemPrompt = "Você é um assistente especializado em criar documentos profissionais. " +
                "Sempre responda no formato: TÍTULO: [título do documento] | CONTEÚDO: [conteúdo detalhado do documento]";

        String userPrompt = "Crie um documento completo baseado neste prompt: " + prompt;
        if (additionalContext != null && !additionalContext.isBlank()) {
            userPrompt += "\n\nContexto adicional: " + additionalContext;
        }

        List<OpenAiMessage> messages = List.of(
                new OpenAiMessage("system", systemPrompt),
                new OpenAiMessage("user", userPrompt)
        );

        OpenAiRequest requestBody = new OpenAiRequest("gpt-3.5-turbo", messages);

        OpenAiResponse response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(OpenAiResponse.class);

        String aiResponse = response.getSummary();
        return parseGeneratedDocument(aiResponse);
    }

    public List<GeneratedDocumentResponse> replicateDocumentByPurpose(
            String documentText,
            String purpose,
            int numberOfReplicas) {

        log.info("Replicating document for purpose: {} ({} replicas)", purpose, numberOfReplicas);

        List<GeneratedDocumentResponse> replicas = new ArrayList<>();

        String systemPrompt = "Você é um assistente que adapta documentos para diferentes contextos e propósitos. " +
                "Sempre responda no formato: TÍTULO: [título do documento] | CONTEÚDO: [conteúdo adaptado]";

        for (int i = 0; i < numberOfReplicas; i++) {
            String userPrompt = String.format(
                    "Adapte o seguinte documento para o propósito: %s. " +
                            "Crie uma variação %d com abordagem diferente mas mantendo a essência.\n\n" +
                            "Documento original:\n%s",
                    purpose, i + 1, documentText
            );

            List<OpenAiMessage> messages = List.of(
                    new OpenAiMessage("system", systemPrompt),
                    new OpenAiMessage("user", userPrompt)
            );

            OpenAiRequest requestBody = new OpenAiRequest("gpt-3.5-turbo", messages);

            try {
                OpenAiResponse response = restClient.post()
                        .uri("/chat/completions")
                        .body(requestBody)
                        .retrieve()
                        .body(OpenAiResponse.class);

                String aiResponse = response.getSummary();
                GeneratedDocumentResponse replica = parseGeneratedDocument(aiResponse);
                replicas.add(replica);

                if (i < numberOfReplicas - 1) {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                log.error("Error generating replica {}: {}", i + 1, e.getMessage());
            }
        }

        return replicas;
    }

    public ImprovedDocumentResponse improveDocument(String documentText, String improvementGuidelines) {
        log.info("Improving document with guidelines: {}", improvementGuidelines);

        String systemPrompt = "Você é um revisor e editor profissional de documentos. " +
                "Sempre responda no formato: MELHORADO: [versão melhorada do documento] | " +
                "RESUMO: [resumo das melhorias aplicadas]";

        String userPrompt = "Melhore o seguinte documento aplicando correções gramaticais, " +
                "melhorias na clareza, estrutura e profissionalismo.";

        if (improvementGuidelines != null && !improvementGuidelines.isBlank()) {
            userPrompt += "\n\nDiretrizes específicas: " + improvementGuidelines;
        }

        userPrompt += "\n\nDocumento:\n" + documentText;

        List<OpenAiMessage> messages = List.of(
                new OpenAiMessage("system", systemPrompt),
                new OpenAiMessage("user", userPrompt)
        );

        OpenAiRequest requestBody = new OpenAiRequest("gpt-3.5-turbo", messages);

        OpenAiResponse response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(OpenAiResponse.class);

        String aiResponse = response.getSummary();
        return parseImprovedDocument(aiResponse);
    }

    private GeneratedDocumentResponse parseGeneratedDocument(String aiResponse) {
        Pattern pattern = Pattern.compile("TÍTULO:\\s*(.+?)\\s*\\|\\s*CONTEÚDO:\\s*(.+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (matcher.find()) {
            String title = matcher.group(1).trim();
            String content = matcher.group(2).trim();

            return GeneratedDocumentResponse.builder()
                    .title(title)
                    .content(content)
                    .generatedBy("OpenAI GPT-3.5")
                    .build();
        }

        return GeneratedDocumentResponse.builder()
                .title("Documento Gerado por IA")
                .content(aiResponse)
                .generatedBy("OpenAI GPT-3.5")
                .build();
    }

    private ImprovedDocumentResponse parseImprovedDocument(String aiResponse) {
        Pattern pattern = Pattern.compile("MELHORADO:\\s*(.+?)\\s*\\|\\s*RESUMO:\\s*(.+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (matcher.find()) {
            String improved = matcher.group(1).trim();
            String summary = matcher.group(2).trim();

            return ImprovedDocumentResponse.builder()
                    .improvedContent(improved)
                    .improvementsSummary(summary)
                    .build();
        }

        return ImprovedDocumentResponse.builder()
                .improvedContent(aiResponse)
                .improvementsSummary("Documento melhorado com sucesso.")
                .build();
    }

    public MarkdownDocumentResponse generateMarkdownDocument(String prompt, String additionalContext) {
        log.info("Generating markdown document from prompt: {}", prompt);

        String systemPrompt = "Você é um assistente especializado em criar documentos profissionais formatados em Markdown. " +
                "Sempre responda no formato: TÍTULO: [título do documento] | MARKDOWN: [conteúdo completo em Markdown]. " +
                "Use formatação Markdown adequada: # para títulos principais, ## para subtítulos, ### para seções, " +
                "**negrito** para ênfase, *itálico* quando apropriado, listas com - ou 1., e parágrafos bem estruturados. " +
                "Adicione um recuo lógico no início dos parágrafos usando espaços quando apropriado.";

        String userPrompt = "Crie um documento completo e bem formatado em Markdown baseado neste prompt: " + prompt;
        if (additionalContext != null && !additionalContext.isBlank()) {
            userPrompt += "\n\nContexto adicional: " + additionalContext;
        }

        List<OpenAiMessage> messages = List.of(
                new OpenAiMessage("system", systemPrompt),
                new OpenAiMessage("user", userPrompt)
        );

        OpenAiRequest requestBody = new OpenAiRequest("gpt-5-mini", messages);

        OpenAiResponse response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(OpenAiResponse.class);

        String aiResponse = response.getSummary();
        return parseMarkdownDocument(aiResponse);
    }

    private MarkdownDocumentResponse parseMarkdownDocument(String aiResponse) {
        Pattern pattern = Pattern.compile("TÍTULO:\\s*(.+?)\\s*\\|\\s*MARKDOWN:\\s*(.+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (matcher.find()) {
            String title = matcher.group(1).trim();
            String markdownContent = matcher.group(2).trim();

            return new MarkdownDocumentResponse(title, markdownContent);
        }

        String[] lines = aiResponse.split("\n", 2);
        if (lines.length > 0) {
            String title = lines[0].replaceAll("^#\\s*", "").trim();
            String content = lines.length > 1 ? lines[1].trim() : aiResponse;
            return new MarkdownDocumentResponse(title, content);
        }

        return new MarkdownDocumentResponse("Documento Markdown Gerado por IA", aiResponse);
    }
}
