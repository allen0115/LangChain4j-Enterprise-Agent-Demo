package com.example.aiagent.controller;

import com.example.aiagent.dto.DocumentResponse;
import com.example.aiagent.model.DocumentMetadata;
import com.example.aiagent.service.IngestionFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocumentController {

    private final IngestionFacadeService ingestionFacadeService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", defaultValue = "default-user") String userId) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DocumentMetadata metadata = ingestionFacadeService.ingest(file, userId);
        return ResponseEntity.ok(mapToResponse(metadata));
    }

    private DocumentResponse mapToResponse(DocumentMetadata metadata) {
        return DocumentResponse.builder()
                .id(metadata.getId())
                .fileName(metadata.getFileName())
                .userId(metadata.getUserId())
                .contentType(metadata.getContentType())
                .fileSize(metadata.getFileSize())
                .status(metadata.getStatus())
                .uploadedAt(metadata.getUploadedAt())
                .errorMessage(metadata.getErrorMessage())
                .build();
    }
}
