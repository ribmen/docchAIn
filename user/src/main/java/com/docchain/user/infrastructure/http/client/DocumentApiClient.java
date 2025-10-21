package com.docchain.user.infrastructure.http.client;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@HttpExchange(url = "/api/v1/documents")
public interface DocumentApiClient {

    @PostExchange
    DocumentResponseDto createDocument(@RequestBody CreateDocumentRequest request);

    @GetExchange("/{userId}")
    List<DocumentResponseDto> findAllDocumentsForUser(@PathVariable UUID userId);

    @PostExchange("/owner/{userId}/bulk")
    List<DocumentResponseDto> createManyDocuments(@RequestBody List<CreateDocumentRequest> requests,
                                                  @PathVariable("userId") UUID ownerId);
}
