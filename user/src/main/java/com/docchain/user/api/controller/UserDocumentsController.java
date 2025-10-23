package com.docchain.user.api.controller;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.DocumentUpdateRequest;
import com.docchain.user.domain.service.UserDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userDocuments")
@RequiredArgsConstructor
public class UserDocumentsController {

    private final UserDocumentService userDocumentService;

    @PostMapping("/{userId}")
    public ResponseEntity<DocumentResponseDto> createDocumentForUser(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateDocumentRequest request) {

        DocumentResponseDto document = userDocumentService.createDocumentForUser(
                userId,
                request.getTitle(),
                request.getContent()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PostMapping("/{ownerId}/bulk")
    public ResponseEntity<List<DocumentResponseDto>> createManyDocumentsForUser(
            @RequestBody List<CreateDocumentRequest> requests,
            @PathVariable UUID ownerId) {

        List<DocumentResponseDto> documents = userDocumentService.createManyDocumentsForUser(
                ownerId,
                requests
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(documents);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<DocumentResponseDto>> getUserDocuments(@PathVariable UUID userId) {

        List<DocumentResponseDto> documents = userDocumentService.findAllDocumentsForUser(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/titleSearch/{userId}")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByTitle(@PathVariable UUID userId, @RequestParam String docTitle) {
        List<DocumentResponseDto> document = userDocumentService.findDocumentForUserByTitle(userId, docTitle);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{userId}/documents/{documentId}")
    public ResponseEntity<DocumentResponseDto> updateDocumentForUser(
            @PathVariable UUID userId,
            @PathVariable UUID documentId,
            @Valid @RequestBody DocumentUpdateRequest updateRequest) {

        DocumentResponseDto updatedDocument = userDocumentService.updateDocumentForUser(
                userId,
                documentId,
                updateRequest
        );

        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{userId}/{documentId}")
    public ResponseEntity<Void> deleteDocumentForUser(
            @PathVariable UUID userId,
            @PathVariable UUID documentId) {

        userDocumentService.deleteDocumentForUser(userId, documentId);
        return ResponseEntity.noContent().build();
    }
}
