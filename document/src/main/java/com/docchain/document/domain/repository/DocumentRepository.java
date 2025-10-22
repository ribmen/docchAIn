package com.docchain.document.domain.repository;

import com.docchain.document.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByOwnerId(UUID ownerId);

    @Query("select d from Document d where d.ownerId = :ownerId and d.title like %:docTitle%")
    Optional<List<Document>> findByOwnerIdAndDocTitle(UUID ownerId, String docTitle);

    Optional<Document> findByIdAndOwnerId(UUID id, UUID ownerId);
    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);
}
