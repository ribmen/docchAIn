package com.docchain.user.domain.service;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.domain.exception.DocumentServiceIntegrationException;
import com.docchain.user.domain.exception.UserNotFoundException;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
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
        } catch (RestClientException e) {
            throw new DocumentServiceIntegrationException("Falha ao criar documento no serviço externo.", e);
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
        } catch (RestClientException e) {
            throw new DocumentServiceIntegrationException("Falha ao criar múltiplos documentos no serviço externo.", e);
        }
    }

    public List<DocumentResponseDto> findAllDocumentsForUser(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }

        try {
            return documentServiceClient.findAllDocumentsForUser(userId);
        } catch (RestClientException e) {
            throw new DocumentServiceIntegrationException("Falha ao buscar documentos do usuário no serviço externo.", e);
        }
    }

}
