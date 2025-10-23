package com.docchain.user.graphql;

import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import com.docchain.user.domain.service.UserDocumentService;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
public class DocumentGraphqlController {

    @Autowired
    private UserDocumentService userDocumentService;

    private final DocumentApiClient documentApiClient;

    public DocumentGraphqlController(DocumentApiClient documentApiClient) {
        this.documentApiClient = documentApiClient;
    }

    @QueryMapping
    @Cacheable(value = "documentsByUser", key = "#userId")
    public List<DocumentResponseDto> documentsByUser(@Argument UUID userId) {

        log.info("QUERYING DOCUMENTS FOR USER {} >>> WITHOUT CACHE <<<", userId);
        List<DocumentResponseDto> documents = documentApiClient.findAllDocumentsForUser(userId);

        return Optional.ofNullable(documents).orElse(Collections.emptyList());
    }

    @QueryMapping
    @Cacheable(value = "findDocumentByUserAndDocName", key = "#userId")
    public List<DocumentResponseDto> findDocumentByUserAndDocName(@Argument UUID userId, @Argument String docTitle) {

        log.info("QUERYING SINGLE DOCUMENT FOR USER {} >>> WITHOUT CACHE <<<", userId);

        List<DocumentResponseDto> documents = userDocumentService.findDocumentForUserByTitle(userId, docTitle);
        return Optional.ofNullable(documents).orElse(Collections.emptyList());
    }

    @MutationMapping
    @CacheEvict(value = {"documentsByUser", "findDocumentByUserAndDocName"}, key = "#userId")
    public DocumentResponseDto updateDocument(@Argument UUID userId, 
                                              @Argument UUID documentId,
                                              @Argument String title,
                                              @Argument String content) {

        log.info("UPDATING DOCUMENT {} FOR USER {} >>> EVICTING CACHE <<<", documentId, userId);

        DocumentUpdateRequest updateRequest = DocumentUpdateRequest.builder()
                .title(title)
                .content(content)
                .build();

        return userDocumentService.updateDocumentForUser(userId, documentId, updateRequest);
    }

    @MutationMapping
    @CacheEvict(value = {"documentsByUser", "findDocumentByUserAndDocName"}, key = "#userId")
    public Boolean deleteDocument(@Argument UUID userId, @Argument UUID documentId) {

        log.info("DELETING DOCUMENT {} FOR USER {} >>> EVICTING CACHE <<<", documentId, userId);

        try {
            userDocumentService.deleteDocumentForUser(userId, documentId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting document {} for user {}", documentId, userId, e);
            return false;
        }
    }
}
