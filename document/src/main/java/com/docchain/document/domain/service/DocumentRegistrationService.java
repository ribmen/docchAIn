package com.docchain.document.domain.service;

import com.docchain.document.api.model.DocumentInput;
import com.docchain.document.api.model.DocumentResponseDto;
import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.repository.DocumentRepository;
import com.docchain.document.infrastructure.pdf.MarkdownToPdfConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentRegistrationService {

    private final DocumentRepository documentRepository;
    private final MarkdownToPdfConverter markdownToPdfConverter;

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

    @Transactional
    public List<DocumentResponseDto> createManyDocuments(List<DocumentInput> inputs, UUID ownerId) {
        List<Document> documents = inputs
                .stream()
                .map(input -> Document.brandNew(
                                ownerId,
                                input.getTitle(),
                                input.getContent())
                        )
                .toList();
        documentRepository.saveAll(documents);

        return documents.stream()
                .map(DocumentResponseDto::from)
                .toList();
    }

    public Document findById(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found."));
    }

    public void delete(UUID ownerId, UUID documentId) {

        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID must be provided to delete a document.");
        }
        Document current = documentRepository.findByIdAndOwnerId(documentId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado para o dono."));
        documentRepository.delete(current);
    }

    public List<Document> listByOwner(UUID ownerId) {
        return documentRepository.findByOwnerId(ownerId);
    }

    public List<Document> findByOwnerIdAndDocTitleContaining(UUID ownerId, String docTitle) {
        return documentRepository.findByOwnerIdAndDocTitle(ownerId, docTitle)
                .orElseThrow(() -> new IllegalArgumentException("Document not found for the owner or by title."));
    }

    @Transactional
    public Document createWithMarkdown(UUID ownerId, String title, String markdownContent) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID must be set to create a document.");
        }

        Document document = Document.brandNew(ownerId, title, markdownContent);

        byte[] pdfBytes = markdownToPdfConverter.convertToPdf(markdownContent, title);
        document.setPdfBin(pdfBytes);

        return documentRepository.saveAndFlush(document);
    }
}
