package com.example.aiagent.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Configuration
public class RagConfig {

    @Value("${langchain4j.zhipu.embedding-model.api-key}")
    private String zhipuApiKey;

    // 1. 定义向量存储
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    // 2. 定义智谱 Embedding 模型
    @Bean
    public EmbeddingModel embeddingModel() {
        return ZhipuAiEmbeddingModel.builder()
                .apiKey(zhipuApiKey)
                .model("embedding-2") // 智谱的通用 Embedding 模型
                .build();
    }

    // 3. 定义检索器
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();
    }

    // 4. 启动时加载数据
    @Bean
    public ApplicationRunner ingestDocs(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return args -> {
            // 示例：将项目中的 ADR 文档加载到向量库
            // 确保 docs/adr/0001-enterprise-ai-agent-framework-selection.md 存在
            try {
                Document document = loadDocument("docs/adr/0001-enterprise-ai-agent-framework-selection.md", new TextDocumentParser());
                
                EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .build();
                
                ingestor.ingest(document);
                System.out.println("✅ 已将 ADR 文档加载到长期记忆中");
            } catch (Exception e) {
                System.out.println("⚠️ 加载文档失败 (可能是文件不存在): " + e.getMessage());
            }
        };
    }
}
