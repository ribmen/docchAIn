package com.docchain.aiservice.api.model.response;

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
