package com.docchain.aiservice.api.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDocumentRequest {
    
    @NotBlank(message = "Prompt is required")
    private String prompt;
    
    private String additionalContext;
}
