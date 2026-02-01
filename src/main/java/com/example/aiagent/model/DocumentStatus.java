package com.example.aiagent.model;

public enum DocumentStatus {
    PENDING,    // Uploaded, waiting for ingestion
    INGESTED,   // Successfully vectorized and stored
    FAILED      // Processing failed
}
