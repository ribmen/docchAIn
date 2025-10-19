package com.docchain.document.domain.service;

import com.docchain.document.api.model.DocumentInput;
import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentRegistrationService {

    private final DocumentRepository documentRepository;

    public Document create(DocumentInput documentInput) {
        if (documentInput.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID must be set to create a document.");
        }

        Document document = Document.brandNew(
                documentInput.getOwnerId(),
                documentInput.getTitle(),
                documentInput.getContent()
        );
        return documentRepository.saveAndFlush(document);
    }

    public Document findById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found."));
    }

    public Document update(UUID ownerId, UUID documentId, Document changes) {
        Document current = documentRepository.findByIdAndOwnerId(documentId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado para o dono."));
        // aplicar alterações necessárias
        current.setTitle(changes.getTitle());
        current.setContent(changes.getContent());
        return documentRepository.save(current);
    }

    public void delete(UUID ownerId, UUID documentId) {
        Document current = documentRepository.findByIdAndOwnerId(documentId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado para o dono."));
        documentRepository.delete(current);
    }

    public List<Document> listByOwner(UUID ownerId) {
        return documentRepository.findByOwnerId(ownerId);
    }
}
