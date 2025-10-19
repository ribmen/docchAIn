package com.docchain.user.domain.service;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDocumentService {

    private final UserRepository userRepository;
    private final DocumentApiClient documentServiceClient;

    public DocumentResponseDto createDocumentForUser(UUID userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        CreateDocumentRequest req = new CreateDocumentRequest(userId, title, content);
        DocumentResponseDto response = documentServiceClient.createDocument(req);

        user.addDocument(response.id());
        userRepository.save(user);

        return documentServiceClient.createDocument(req);
    }
}
