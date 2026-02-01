package com.example.aiagent.service.loader;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentLoaderFactory {

    private final List<DocumentLoaderStrategy> strategies;

    public DocumentLoaderFactory(List<DocumentLoaderStrategy> strategies) {
        this.strategies = strategies;
    }

    public DocumentLoaderStrategy getLoader(String contentType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported content type: " + contentType));
    }
}
