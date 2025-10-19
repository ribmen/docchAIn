package com.docchain.document.api.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentInput {
    private UUID ownerId;
    private String title;
    private String content;
}
