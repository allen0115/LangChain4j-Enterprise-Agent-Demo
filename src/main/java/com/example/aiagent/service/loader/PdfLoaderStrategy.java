package com.example.aiagent.service.loader;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PdfLoaderStrategy implements DocumentLoaderStrategy {

    @Override
    public boolean supports(String contentType) {
        return "application/pdf".equals(contentType);
    }

    @Override
    public Document load(Path filePath) {
        return FileSystemDocumentLoader.loadDocument(filePath, new ApacheTikaDocumentParser());
    }
}
