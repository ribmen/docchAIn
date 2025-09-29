package com.docchain.document.domain.model;

import com.docchain.document.domain.enums.DocumentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Document {

    @Id
    private UUID id;

    private String fileName;

    private String title;

    private String content;

    private UUID ownerId;

    private DocumentStatus status;

    // metadata

    private Date createdAt;

    private Date updatedAt;

    private String format;

    private int version;








}
