package com.docchain.document.api.controller;

import com.docchain.document.api.model.DocumentInput;
import com.docchain.document.api.model.DocumentResponseDto;
import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.service.DocumentEditService;
import com.docchain.document.domain.service.DocumentRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentController {
    
    private final DocumentRegistrationService documentRegistrationService;
    private final DocumentEditService documentEditService;

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody DocumentInput request) {
        Document document = documentRegistrationService.create(
            request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PostMapping("/owner/{ownerId}/bulk")
    public ResponseEntity<List<DocumentResponseDto>> createManyDocuments(
            @PathVariable UUID ownerId,
            @RequestBody List<DocumentInput> request) {
        List<DocumentResponseDto> documents = documentRegistrationService.createManyDocuments(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(documents);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserId(@PathVariable UUID ownerId) {

        List<Document> documents = documentRegistrationService.listByOwner(ownerId);
        return ResponseEntity.ok(
                documents.stream()
                        .map(DocumentResponseDto::from)
                        .toList()
        );
    }

    @GetMapping("/owner/search/{ownerId}")
    public ResponseEntity<List<DocumentResponseDto>> findByOwnerIdAndDocTitleContaining(@PathVariable UUID ownerId, @RequestParam String docTitle) {

        List<Document> documents = documentRegistrationService.findByOwnerIdAndDocTitleContaining(ownerId, docTitle);
        return ResponseEntity.ok(
                documents.stream()
                        .map(DocumentResponseDto::from)
                        .toList()
        );
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Document> getDocument(@PathVariable UUID documentId) {
        Document document = documentRegistrationService.findById(documentId);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDto> editDocument(
            @RequestParam UUID ownerId,
            @PathVariable UUID documentId,
            @RequestBody Document changes) {

        Document updatedDocument = documentEditService.update(ownerId, documentId, changes);
        return ResponseEntity.ok(DocumentResponseDto.from(updatedDocument));
    }

    @DeleteMapping("/{ownerId}/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID ownerId,
            @PathVariable UUID documentId) {

        documentRegistrationService.delete(ownerId, documentId);
        return ResponseEntity.noContent().build();
    }
}