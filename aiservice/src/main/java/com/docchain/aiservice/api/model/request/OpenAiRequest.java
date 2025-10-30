package com.docchain.aiservice.api.model.request;

import java.util.List;

public record OpenAiRequest(
        String model,
        List<OpenAiMessage> messages
) {}
