package com.docchain.user.api.model;

import java.util.UUID;

public record DocumentResponseDto(
        UUID id,
        String title,
        String content,
        UUID ownerId,
        String status,
        String createdAt
) {}
