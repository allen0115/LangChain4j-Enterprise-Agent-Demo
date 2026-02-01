package com.example.aiagent.dto;

import com.example.aiagent.model.DocumentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private String fileName;
    private String userId;
    private String contentType;
    private Long fileSize;
    private DocumentStatus status;
    private LocalDateTime uploadedAt;
    private String errorMessage;
}
