package com.example.aiagent.service.loader;

import dev.langchain4j.data.document.Document;
import java.nio.file.Path;

public interface DocumentLoaderStrategy {
    boolean supports(String contentType);
    Document load(Path filePath);
}
