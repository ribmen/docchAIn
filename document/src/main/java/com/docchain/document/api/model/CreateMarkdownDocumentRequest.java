package com.docchain.document.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarkdownDocumentRequest {

    private UUID ownerId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Markdown content is required")
    private String markdownContent;
}
