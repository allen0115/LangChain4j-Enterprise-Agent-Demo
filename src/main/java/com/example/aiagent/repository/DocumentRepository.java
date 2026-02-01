package com.example.aiagent.repository;

import com.example.aiagent.model.DocumentMetadata;
import com.example.aiagent.model.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
    List<DocumentMetadata> findByUserId(String userId);
    List<DocumentMetadata> findByStatus(DocumentStatus status);
}
