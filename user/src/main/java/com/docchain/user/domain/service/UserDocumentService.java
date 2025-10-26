package com.docchain.user.domain.service;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import com.docchain.user.domain.exception.DocumentServiceIntegrationException;
import com.docchain.user.domain.exception.UserNotFoundException;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDocumentService {

    private final UserRepository userRepository;
    private final DocumentApiClient documentServiceClient;
    private final CircuitBreaker documentServiceCircuitBreaker;
    private final Retry documentServiceRetry;
    private final TimeLimiter documentServiceTimeLimiter;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private <T> T executeWithResilience(Supplier<T> supplier, T fallbackValue) {
        Supplier<T> decoratedSupplier = CircuitBreaker
                .decorateSupplier(documentServiceCircuitBreaker,
                        Retry.decorateSupplier(documentServiceRetry, supplier));

        Callable<T> restrictedCall = TimeLimiter.decorateFutureSupplier(
                documentServiceTimeLimiter,
                () -> CompletableFuture.supplyAsync(decoratedSupplier, executorService)
        );

        try {
            return restrictedCall.call();
        } catch (Exception e) {
            if (fallbackValue != null) {
                return fallbackValue;
            }
            throw new DocumentServiceIntegrationException("Falha ao se comunicar com o serviço de documentos.", e);
        }
    }

    public DocumentResponseDto createDocumentForUser(UUID userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        CreateDocumentRequest req = new CreateDocumentRequest(userId, title, content);
        DocumentResponseDto response = executeWithResilience(
                () -> documentServiceClient.createDocument(req),
                null
        );

        if (response != null) {
            user.addDocument(response.id());
            userRepository.save(user);
        }

        return response;
    }

    public List<DocumentResponseDto> createManyDocumentsForUser(UUID userId, List<CreateDocumentRequest> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        List<DocumentResponseDto> responses = executeWithResilience(
                () -> documentServiceClient.createManyDocuments(requests, userId),
                Collections.emptyList()
        );

        responses.forEach(doc -> user.addDocument(doc.id()));
        userRepository.save(user);

        return responses;
    }

    public List<DocumentResponseDto> findAllDocumentsForUser(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        return executeWithResilience(
                () -> documentServiceClient.findAllDocumentsForUser(userId),
                Collections.emptyList()
        );
    }

    public List<DocumentResponseDto> findDocumentForUserByTitle(UUID userId, String docTitle) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        return executeWithResilience(
                () -> documentServiceClient.findDocumentByUserAndDocName(userId, docTitle),
                Collections.emptyList()
        );
    }

    public DocumentResponseDto updateDocumentForUser(UUID userId, UUID documentId, DocumentUpdateRequest updateRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        return executeWithResilience(
                () -> documentServiceClient.updateDocument(documentId, userId, updateRequest),
                null
        );
    }

    public void deleteDocumentForUser(UUID userId, UUID documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        executeWithResilience(() -> {
            documentServiceClient.deleteDocument(documentId, userId);
            return null;
        }, null);

        user.removeDocument(documentId);
        userRepository.save(user);
    }

}
