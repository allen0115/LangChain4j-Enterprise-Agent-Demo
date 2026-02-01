package com.example.aiagent.service;

import com.example.aiagent.exception.IngestionException;
import com.example.aiagent.model.DocumentMetadata;
import com.example.aiagent.model.DocumentStatus;
import com.example.aiagent.repository.DocumentRepository;
import com.example.aiagent.service.loader.DocumentLoaderFactory;
import com.example.aiagent.service.loader.DocumentLoaderStrategy;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionFacadeService {

    private final FileStorageService fileStorageService;
    private final DocumentRepository documentRepository;
    private final DocumentLoaderFactory documentLoaderFactory;
    private final EmbeddingStoreIngestor embeddingStoreIngestor;

    public DocumentMetadata ingest(MultipartFile file, String userId) {
        String contentType = file.getContentType();
        log.info("Starting ingestion for file: {}, type: {}, user: {}", file.getOriginalFilename(), contentType, userId);

        // 1. Store File
        String storagePath = fileStorageService.storeFile(file);
        
        // 2. Save Metadata (PENDING)
        DocumentMetadata metadata = DocumentMetadata.builder()
                .fileName(file.getOriginalFilename())
                .userId(userId)
                .contentType(contentType)
                .fileSize(file.getSize())
                .storagePath(storagePath)
                .status(DocumentStatus.PENDING)
                .build();
        metadata = documentRepository.save(metadata);

        try {
            // 3. Load Document
            DocumentLoaderStrategy loader = documentLoaderFactory.getLoader(contentType);
            Document document = loader.load(Paths.get(storagePath));
            
            // Add metadata to LangChain4j Document
            document.metadata().put("file_name", metadata.getFileName());
            document.metadata().put("user_id", metadata.getUserId());
            document.metadata().put("db_id", String.valueOf(metadata.getId()));

            // 4. Ingest to Vector Store
            embeddingStoreIngestor.ingest(document);

            // 5. Update Status (INGESTED)
            metadata.setStatus(DocumentStatus.INGESTED);
            documentRepository.save(metadata);
            log.info("Successfully ingested document id: {}", metadata.getId());

        } catch (Exception e) {
            log.error("Ingestion failed for document id: {}", metadata.getId(), e);
            metadata.setStatus(DocumentStatus.FAILED);
            metadata.setErrorMessage(e.getMessage());
            documentRepository.save(metadata);
            throw new IngestionException("Failed to ingest document", e);
        }

        return metadata;
    }
}
