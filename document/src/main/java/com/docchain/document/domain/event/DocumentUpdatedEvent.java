package com.docchain.document.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class DocumentUpdatedEvent {
    private final OffsetDateTime occurredAt;
    private final UUID documentId;
}
