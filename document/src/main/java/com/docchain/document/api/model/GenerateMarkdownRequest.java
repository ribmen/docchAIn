package com.docchain.document.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMarkdownRequest {
    private String prompt;
    private String additionalContext;
}
