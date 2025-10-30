package com.docchain.user.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplicateDocumentAIRequest {
    private String purpose;
    private int numberOfReplicas = 3;
}
