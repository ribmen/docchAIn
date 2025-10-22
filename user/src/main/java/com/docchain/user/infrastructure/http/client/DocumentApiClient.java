package com.docchain.user.infrastructure.http.client;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@HttpExchange(url = "/api/v1/documents")
public interface DocumentApiClient {

    @PostExchange
    DocumentResponseDto createDocument(@RequestBody CreateDocumentRequest request);

    @GetExchange("/owner/{userId}")
    List<DocumentResponseDto> findAllDocumentsForUser(@PathVariable UUID userId);

    @GetExchange("/owner/search/{userId}")
    List<DocumentResponseDto> findDocumentByUserAndDocName(@PathVariable UUID userId, @RequestParam String docTitle);

    @PostExchange("/owner/{userId}/bulk")
    List<DocumentResponseDto> createManyDocuments(@RequestBody List<CreateDocumentRequest> requests,
                                                  @PathVariable("userId") UUID ownerId);

    @PutExchange("/{documentId}")
    DocumentResponseDto updateDocument(@PathVariable UUID documentId,
                                      @RequestParam UUID ownerId,
                                      @RequestBody DocumentUpdateRequest changes);

    @DeleteExchange("/{documentId}")
    void deleteDocument(@PathVariable UUID documentId,
                       @RequestParam UUID ownerId);
}
