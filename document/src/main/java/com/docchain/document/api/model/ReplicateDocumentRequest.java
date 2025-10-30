package com.docchain.document.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplicateDocumentRequest {
    private String documentText;
    private String purpose;
    private int numberOfReplicas = 3;
}
