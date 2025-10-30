package com.docchain.aiservice.api.model.response;

import com.docchain.aiservice.api.model.request.OpenAiMessage;

public record OpenAiChoice(
        OpenAiMessage message
) {}
