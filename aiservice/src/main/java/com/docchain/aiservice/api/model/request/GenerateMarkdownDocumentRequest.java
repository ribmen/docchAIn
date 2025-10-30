package com.docchain.aiservice.api.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMarkdownDocumentRequest {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    private String additionalContext;
}
