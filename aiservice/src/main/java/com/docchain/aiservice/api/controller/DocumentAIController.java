package com.docchain.aiservice.api.controller;

import com.docchain.aiservice.api.model.request.GenerateDocumentRequest;
import com.docchain.aiservice.api.model.request.GenerateMarkdownDocumentRequest;
import com.docchain.aiservice.api.model.request.ImproveDocumentRequest;
import com.docchain.aiservice.api.model.request.ReplicateDocumentRequest;
import com.docchain.aiservice.api.model.response.GeneratedDocumentResponse;
import com.docchain.aiservice.api.model.response.ImprovedDocumentResponse;
import com.docchain.aiservice.api.model.response.MarkdownDocumentResponse;
import com.docchain.aiservice.domain.service.DocumentAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentAIController {

    private final DocumentAIService documentAIService;

    @PostMapping("/generate")
    public ResponseEntity<GeneratedDocumentResponse> generateDocumentText(
            @Valid @RequestBody GenerateDocumentRequest request) {

        log.info("Received request to generate document from prompt");

        GeneratedDocumentResponse response = documentAIService.generateDocumentText(
                request.getPrompt(),
                request.getAdditionalContext()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/replicate")
    public ResponseEntity<List<GeneratedDocumentResponse>> replicateDocumentByPurpose(
            @Valid @RequestBody ReplicateDocumentRequest request) {

        log.info("Received request to replicate document for purpose: {}", request.getPurpose());

        List<GeneratedDocumentResponse> responses = documentAIService.replicateDocumentByPurpose(
                request.getDocumentText(),
                request.getPurpose(),
                request.getNumberOfReplicas()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PostMapping("/improve")
    public ResponseEntity<ImprovedDocumentResponse> improveDocument(
            @Valid @RequestBody ImproveDocumentRequest request) {

        log.info("Received request to improve document");

        ImprovedDocumentResponse response = documentAIService.improveDocument(
                request.getDocumentText(),
                request.getImprovementGuidelines()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-markdown")
    public ResponseEntity<MarkdownDocumentResponse> generateMarkdownDocument(
            @Valid @RequestBody GenerateMarkdownDocumentRequest request) {

        log.info("Received request to generate markdown document from prompt");

        MarkdownDocumentResponse response = documentAIService.generateMarkdownDocument(
                request.getPrompt(),
                request.getAdditionalContext()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
