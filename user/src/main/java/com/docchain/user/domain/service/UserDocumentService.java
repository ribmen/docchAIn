package com.docchain.user.domain.service;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import com.docchain.user.api.model.GenerateDocumentAIRequest;
import com.docchain.user.domain.exception.BadGatewayException;
import com.docchain.user.domain.exception.ServiceUnavailableException;
import com.docchain.user.domain.exception.UserNotFoundException;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDocumentService {

    private final UserRepository userRepository;
    private final DocumentApiClient documentServiceClient;

    public DocumentResponseDto createDocumentForUser(UUID userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            CreateDocumentRequest req = new CreateDocumentRequest(userId, title, content);
            DocumentResponseDto response = documentServiceClient.createDocument(req);
            
            user.addDocument(response.id());
            userRepository.save(user);
            
            return response;
            
        } catch (ResourceAccessException e) {
            log.error("Timeout accessing document service for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error from document service for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public List<DocumentResponseDto> createManyDocumentsForUser(UUID userId, List<CreateDocumentRequest> requests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            List<DocumentResponseDto> responses = documentServiceClient.createManyDocuments(requests, userId);
            
            responses.forEach(doc -> user.addDocument(doc.id()));
            userRepository.save(user);
            
            return responses;
            
        } catch (ResourceAccessException e) {
            log.error("Timeout creating documents for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error creating documents for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public List<DocumentResponseDto> findAllDocumentsForUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        try {
            return documentServiceClient.findAllDocumentsForUser(userId);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout finding documents for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error finding documents for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public List<DocumentResponseDto> findDocumentForUserByTitle(UUID userId, String docTitle) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        try {
            return documentServiceClient.findDocumentByUserAndDocName(userId, docTitle);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout finding documents by title for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error finding documents by title for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public DocumentResponseDto updateDocumentForUser(UUID userId, UUID documentId, DocumentUpdateRequest updateRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        try {
            return documentServiceClient.updateDocument(documentId, userId, updateRequest);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout updating document {} for user {}", documentId, userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error updating document {} for user {}", documentId, userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public void deleteDocumentForUser(UUID userId, UUID documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            documentServiceClient.deleteDocument(documentId, userId);
            user.removeDocument(documentId);
            userRepository.save(user);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout deleting document {} for user {}", documentId, userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error deleting document {} for user {}", documentId, userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public DocumentResponseDto generateDocumentWithAI(UUID userId, String prompt, String additionalContext) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            GenerateDocumentAIRequest request = new GenerateDocumentAIRequest(prompt, additionalContext);
            DocumentResponseDto response = documentServiceClient.generateDocumentWithAI(userId, request);
            
            user.addDocument(response.id());
            userRepository.save(user);
            
            return response;
            
        } catch (ResourceAccessException e) {
            log.error("Timeout generating document with AI for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error generating document with AI for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public List<DocumentResponseDto> replicateDocumentWithAI(
            UUID userId,
            UUID documentId,
            String purpose,
            int numberOfReplicas) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            List<DocumentResponseDto> responses = documentServiceClient.replicateDocumentWithAI(
                    documentId,
                    purpose,
                    numberOfReplicas
            );
            
            responses.forEach(doc -> user.addDocument(doc.id()));
            userRepository.save(user);
            
            return responses;
            
        } catch (ResourceAccessException e) {
            log.error("Timeout replicating document with AI for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error replicating document with AI for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public DocumentResponseDto improveDocumentWithAI(
            UUID userId,
            UUID documentId,
            String improvementGuidelines) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        try {
            return documentServiceClient.improveDocumentWithAI(documentId, improvementGuidelines);
            
        } catch (ResourceAccessException e) {
            log.error("Timeout improving document with AI for user {}", userId, e);
            throw new ServiceUnavailableException("Document service timeout", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error improving document with AI for user {}", userId, e);
            throw new BadGatewayException("Document service error", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open for document service", e);
            throw new ServiceUnavailableException("Document service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Document service not available (LoadBalancer: no instances)", e);
            throw new ServiceUnavailableException("Document service not available", e);
        }
    }

    public DocumentResponseDto generateAndStoreMarkdownDocument(
            UUID userId, 
            String prompt, 
            String additionalContext) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        try {
            log.info("Initiating markdown document generation for user: {}", userId);

            GenerateDocumentAIRequest request = new GenerateDocumentAIRequest(prompt, additionalContext);
            DocumentResponseDto response = documentServiceClient.generateMarkdownDocumentWithAI(userId, request);
            
            log.info("Markdown document with PDF created successfully. ID: {}", response.id());

            user.addDocument(response.id());
            userRepository.save(user);
            
            return response;
            
        } catch (ResourceAccessException e) {
            log.error("Timeout generating markdown document for user {}", userId, e);
            throw new ServiceUnavailableException("Service timeout during markdown document generation", e);
            
        } catch (HttpServerErrorException e) {
            log.error("Server error generating markdown document for user {}", userId, e);
            throw new BadGatewayException("Service error during markdown document generation", e);
            
        } catch (CallNotPermittedException e) {
            log.error("Circuit breaker is open during markdown document generation", e);
            throw new ServiceUnavailableException("Service temporarily unavailable", e);
            
        } catch (IllegalStateException e) {
            log.error("Service not available during markdown document generation", e);
            throw new ServiceUnavailableException("Service not available", e);
        }
    }
}
