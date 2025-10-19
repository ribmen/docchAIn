package com.docchain.document.api.controller;

import com.docchain.document.api.model.DocumentInput;
import com.docchain.document.domain.model.Document;
import com.docchain.document.domain.service.DocumentRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documents")
public class DocumentController {
    
    private final DocumentRegistrationService documentRegistrationService;

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody DocumentInput request) {
        Document document = documentRegistrationService.create(
            request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }
}