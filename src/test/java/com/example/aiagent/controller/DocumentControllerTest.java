package com.example.aiagent.controller;

import com.example.aiagent.model.DocumentMetadata;
import com.example.aiagent.model.DocumentStatus;
import com.example.aiagent.service.IngestionFacadeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestionFacadeService ingestionFacadeService;

    @Test
    void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        DocumentMetadata metadata = DocumentMetadata.builder()
                .id(1L)
                .fileName("test.txt")
                .userId("user1")
                .storagePath("/tmp/test.txt") // Internal path, shouldn't be in response
                .status(DocumentStatus.INGESTED)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(ingestionFacadeService.ingest(any(), anyString())).thenReturn(metadata);

        mockMvc.perform(multipart("/api/docs/upload")
                .file(file)
                .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.status").value("INGESTED"))
                .andExpect(jsonPath("$.storagePath").doesNotExist()); // Verify DTO hides this
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/docs/upload")
                .file(file)
                .param("userId", "user1"))
                .andExpect(status().isBadRequest());
    }
}
