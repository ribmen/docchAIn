package com.docchain.user.infrastructure.http.client;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import com.docchain.user.api.model.GenerateDocumentAIRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange(url = "/api/v1/documents")
public interface DocumentApiClient {

    @PostExchange
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    DocumentResponseDto createDocument(@RequestBody CreateDocumentRequest request);

    @GetExchange("/owner/{userId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    List<DocumentResponseDto> findAllDocumentsForUser(@PathVariable UUID userId);

    @GetExchange("/owner/search/{userId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    List<DocumentResponseDto> findDocumentByUserAndDocName(@PathVariable UUID userId, @RequestParam String docTitle);

    @PostExchange("/owner/{userId}/bulk")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    List<DocumentResponseDto> createManyDocuments(@RequestBody List<CreateDocumentRequest> requests,
                                                  @PathVariable("userId") UUID ownerId);

    @PutExchange("/{documentId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    DocumentResponseDto updateDocument(@PathVariable UUID documentId,
                                      @RequestParam UUID ownerId,
                                      @RequestBody DocumentUpdateRequest changes);

    @DeleteExchange("/{ownerId}/{documentId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    void deleteDocument(@PathVariable UUID documentId,
                       @PathVariable UUID ownerId);

    @PostExchange("/ai/generate")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    DocumentResponseDto generateDocumentWithAI(@RequestParam UUID ownerId,
                                               @RequestBody GenerateDocumentAIRequest request);

    @PostExchange("/ai/generate-markdown")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    DocumentResponseDto generateMarkdownDocumentWithAI(@RequestParam UUID ownerId,
                                                       @RequestBody GenerateDocumentAIRequest request);

    @PostExchange("/ai/replicate/{documentId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    List<DocumentResponseDto> replicateDocumentWithAI(@PathVariable UUID documentId,
                                                      @RequestParam String purpose,
                                                      @RequestParam int numberOfReplicas);

    @PutExchange("/ai/improve/{documentId}")
    @Retry(name = "documentService")
    @CircuitBreaker(name = "documentService")
    DocumentResponseDto improveDocumentWithAI(@PathVariable UUID documentId,
                                              @RequestParam(required = false) String improvementGuidelines);
}
