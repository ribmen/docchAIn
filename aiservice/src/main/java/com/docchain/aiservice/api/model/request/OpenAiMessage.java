package com.docchain.aiservice.api.model.request;

public record OpenAiMessage(
        String role,
        String content
) {}
