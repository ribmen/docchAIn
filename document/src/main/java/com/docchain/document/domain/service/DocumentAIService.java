package com.docchain.document.domain.service;

import com.docchain.document.api.model.DocumentResponseDto;
import com.docchain.document.api.model.GeneratedDocumentResponse;
import com.docchain.document.api.model.ImprovedDocumentResponse;
import com.docchain.document.api.model.MarkdownDocumentResponse;
import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.repository.DocumentRepository;
import com.docchain.document.infrastructure.http.client.AIServiceClient;
import com.docchain.document.infrastructure.pdf.MarkdownToPdfConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAIService {

    private final AIServiceClient aiServiceClient;
    private final DocumentRepository documentRepository;
    private final MarkdownToPdfConverter markdownToPdfConverter;

    @Transactional
    public DocumentResponseDto generateDocument(UUID ownerId, String prompt, String additionalContext) {
        log.info("Generating document for owner: {}", ownerId);

        GeneratedDocumentResponse aiResponse = aiServiceClient.generateDocument(prompt, additionalContext);

        Document document = Document.brandNew(
                ownerId,
                aiResponse.getTitle(),
                aiResponse.getContent()
        );

        Document savedDocument = documentRepository.saveAndFlush(document);
        return DocumentResponseDto.from(savedDocument);
    }

    @Transactional
    public List<DocumentResponseDto> replicateDocument(
            UUID documentId,
            String purpose,
            int numberOfReplicas) {

        log.info("Replicating document {} for purpose: {}", documentId, purpose);

        Document originalDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        List<GeneratedDocumentResponse> aiResponses = aiServiceClient.replicateDocument(
                originalDocument.getContent(),
                purpose,
                numberOfReplicas
        );

        List<Document> replicatedDocuments = aiResponses.stream()
                .map(aiResponse -> Document.brandNew(
                        originalDocument.getOwnerId(),
                        aiResponse.getTitle(),
                        aiResponse.getContent()
                ))
                .toList();

        documentRepository.saveAll(replicatedDocuments);

        return replicatedDocuments.stream()
                .map(DocumentResponseDto::from)
                .toList();
    }

    @Transactional
    public DocumentResponseDto improveDocument(
            UUID documentId,
            String improvementGuidelines) {

        log.info("Improving document {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        ImprovedDocumentResponse aiResponse = aiServiceClient.improveDocument(
                document.getContent(),
                improvementGuidelines
        );

        document.updateContent(aiResponse.getImprovedContent());
        Document updatedDocument = documentRepository.saveAndFlush(document);

        return DocumentResponseDto.from(updatedDocument);
    }

    @Transactional
    public DocumentResponseDto generateMarkdownDocumentWithPdf(
            UUID ownerId, 
            String prompt, 
            String additionalContext) {

        log.info("Generating markdown document with PDF for owner: {}", ownerId);

        MarkdownDocumentResponse aiResponse = aiServiceClient.generateMarkdownDocument(prompt, additionalContext);
        
        log.info("Markdown generated. Title: {}", aiResponse.getTitle());

        Document document = Document.brandNew(
                ownerId,
                aiResponse.getTitle(),
                aiResponse.getMarkdownContent()
        );

        byte[] pdfBytes = markdownToPdfConverter.convertToPdf(
                aiResponse.getMarkdownContent(), 
                aiResponse.getTitle()
        );
        document.setPdfBin(pdfBytes);

        Document savedDocument = documentRepository.saveAndFlush(document);
        
        log.info("Document saved with PDF. ID: {}", savedDocument.getId());

        return DocumentResponseDto.from(savedDocument);
    }
}
