package com.docchain.document.domain.model;

import com.docchain.document.domain.enums.DocumentStatus;
import com.docchain.document.domain.enums.PrivacyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "documents")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Document extends AbstractAggregateRoot<Document> {

    @Id
    private UUID id;

    private String fileName;

    private String title;

    private String content;

    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    // metadata

    @Enumerated(EnumType.STRING)
    private PrivacyStatus privacyStatus;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private String format;

    private int version;

    public static Document brandNew(UUID ownerId, String title, String content) {
        Document document = new Document();
        document.setId(UUID.randomUUID());
        document.setOwnerId(ownerId);
        document.setTitle(title);
        document.setContent(content);
        document.setStatus(DocumentStatus.CREATED);
        document.setVersion(0);
        document.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return document;
    }

    public void changeStatusTo(DocumentStatus newStatus) {
        this.setStatus(newStatus);
    }
}
