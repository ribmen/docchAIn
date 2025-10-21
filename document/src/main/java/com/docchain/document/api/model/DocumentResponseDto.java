package com.docchain.document.api.model;

import com.docchain.document.domain.model.Document;

import java.util.UUID;

public record DocumentResponseDto(
        UUID id,
        String title,
        String content,
        UUID ownerId,
        String status,
        String createdAt
) {
    public static DocumentResponseDto from(Document document) {
        return new DocumentResponseDto(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getOwnerId(),
                document.getStatus().name(),
                document.getCreatedAt().toString()
        );
    }
}
