package com.docchain.document.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkdownDocumentResponse {
    private String title;
    private String markdownContent;
}
