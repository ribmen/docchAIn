package com.docchain.document.domain.service;

import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentEditService {

    //configurações de edição e lifecycle do documento

    private final DocumentRepository documentRepository;

    public Document update(UUID ownerId, UUID documentId, Document changes) {
        Document current = documentRepository.findByIdAndOwnerId(documentId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado para o dono."));

        current.setTitle(changes.getTitle());
        current.setContent(changes.getContent());
        current.setVersion(current.getVersion()+1);
        current.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return documentRepository.save(current);
    }

}
