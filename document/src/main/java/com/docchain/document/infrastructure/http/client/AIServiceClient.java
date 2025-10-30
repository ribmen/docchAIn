package com.docchain.document.infrastructure.http.client;

import com.docchain.document.api.model.GenerateDocumentRequest;
import com.docchain.document.api.model.GenerateMarkdownRequest;
import com.docchain.document.api.model.GeneratedDocumentResponse;
import com.docchain.document.api.model.ImproveDocumentRequest;
import com.docchain.document.api.model.ImprovedDocumentResponse;
import com.docchain.document.api.model.MarkdownDocumentResponse;
import com.docchain.document.api.model.ReplicateDocumentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class AIServiceClient {

    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    public AIServiceClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(200));
        requestFactory.setReadTimeout(Duration.ofSeconds(600));
        
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public GeneratedDocumentResponse generateDocument(String prompt, String additionalContext) {
        log.info("Calling AI Service to generate document");

        String aiServiceUrl = getAIServiceUrl();
        
        GenerateDocumentRequest request = new GenerateDocumentRequest(prompt, additionalContext);

        return restClient.post()
                .uri(aiServiceUrl + "/api/v1/ai/documents/generate")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("Client error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("Error generating document: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Server error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("AI Service unavailable");
                })
                .body(GeneratedDocumentResponse.class);
    }

    public List<GeneratedDocumentResponse> replicateDocument(
            String documentText,
            String purpose,
            int numberOfReplicas) {

        log.info("Calling AI Service to replicate document");

        String aiServiceUrl = getAIServiceUrl();
        
        ReplicateDocumentRequest request = new ReplicateDocumentRequest(
                documentText,
                purpose,
                numberOfReplicas
        );

        return restClient.post()
                .uri(aiServiceUrl + "/api/v1/ai/documents/replicate")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("Client error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("Error replicating document: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Server error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("AI Service unavailable");
                })
                .body(new ParameterizedTypeReference<List<GeneratedDocumentResponse>>() {});
    }

    public ImprovedDocumentResponse improveDocument(
            String documentText,
            String improvementGuidelines) {

        log.info("Calling AI Service to improve document");

        String aiServiceUrl = getAIServiceUrl();
        
        ImproveDocumentRequest request = new ImproveDocumentRequest(
                documentText,
                improvementGuidelines
        );

        return restClient.post()
                .uri(aiServiceUrl + "/api/v1/ai/documents/improve")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("Client error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("Error improving document: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Server error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("AI Service unavailable");
                })
                .body(ImprovedDocumentResponse.class);
    }

    public MarkdownDocumentResponse generateMarkdownDocument(String prompt, String additionalContext) {
        log.info("Calling AI Service to generate markdown document");

        String aiServiceUrl = getAIServiceUrl();
        
        GenerateMarkdownRequest request = new GenerateMarkdownRequest(prompt, additionalContext);

        return restClient.post()
                .uri(aiServiceUrl + "/api/v1/ai/documents/generate-markdown")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("Client error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("Error generating markdown document: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Server error calling AI Service: {}", res.getStatusCode());
                    throw new RuntimeException("AI Service unavailable");
                })
                .body(MarkdownDocumentResponse.class);
    }

    private String getAIServiceUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances("aiservice");
        
        if (instances.isEmpty()) {
            throw new IllegalStateException("AI Service not available");
        }
        
        ServiceInstance instance = instances.get(0);
        String url = instance.getUri().toString();
        log.debug("Using AI Service URL: {}", url);
        
        return url;
    }
}
