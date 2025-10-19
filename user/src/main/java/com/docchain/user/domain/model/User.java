package com.docchain.user.domain.model;

import com.docchain.user.domain.VO.Email;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Table(name = "users")
public class User extends AbstractAggregateRoot<User> {

    @Id
    private UUID id = UUID.randomUUID();

    @Column
    private String username;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(nullable = false, unique = true))
    private Email email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "user_documents", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "document_id")
    private List<UUID> documents = new ArrayList<>();

    public boolean checkPassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.passwordHash);
    }

    public void addDocument(UUID documentId) {
        this.documents.add(documentId);
    }

    public void removeDocument(UUID documentId) {
        this.documents.remove(documentId);
    }

    public static User brandNew(String fullName, String email, String passwordHash) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(new Email(email));
        user.setPasswordHash(passwordHash);
        user.setDocuments(new ArrayList<>());
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

}
