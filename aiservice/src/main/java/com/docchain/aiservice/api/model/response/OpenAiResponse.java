package com.docchain.aiservice.api.model.response;

import java.util.List;

public record OpenAiResponse(
        List<OpenAiChoice> choices
) {
    public String getSummary() {
        if(choices == null || choices.isEmpty()) {
            return "Não foi possível gerar o resumo.";
        }
        return choices.get(0).message().content();
    }
}
