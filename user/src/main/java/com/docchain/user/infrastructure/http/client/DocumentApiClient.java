package com.docchain.user.infrastructure.http.client;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/api/v1/documents")
public interface DocumentApiClient {

    @PostExchange
    DocumentResponseDto createDocument(@RequestBody CreateDocumentRequest request);
}
