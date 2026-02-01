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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Configuration
public class RagConfig {

    private static final Logger logger = LoggerFactory.getLogger(RagConfig.class);

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
        if ("dummy-key".equals(zhipuApiKey) || zhipuApiKey == null || zhipuApiKey.isEmpty()) {
            return createMockEmbeddingModel();
        }
        
        try {
            return ZhipuAiEmbeddingModel.builder()
                    .apiKey(zhipuApiKey)
                    .model("embedding-2")
                    .callTimeout(java.time.Duration.ofSeconds(60))
                    .connectTimeout(java.time.Duration.ofSeconds(60))
                    .writeTimeout(java.time.Duration.ofSeconds(60))
                    .readTimeout(java.time.Duration.ofSeconds(60))
                    .build();
        } catch (Exception e) {
            logger.error("Failed to initialize ZhipuAiEmbeddingModel: {}", e.getMessage());
            logger.warn("Falling back to Mock Embedding Model due to initialization error.");
            return createMockEmbeddingModel();
        }
    }

    private EmbeddingModel createMockEmbeddingModel() {
        logger.warn("Using Mock Embedding Model");
        return new dev.langchain4j.model.embedding.EmbeddingModel() {
            @Override
            public dev.langchain4j.model.output.Response<dev.langchain4j.data.embedding.Embedding> embed(String text) {
                return dev.langchain4j.model.output.Response.from(dev.langchain4j.data.embedding.Embedding.from(new float[1024]));
            }
            
            @Override
            public dev.langchain4j.model.output.Response<dev.langchain4j.data.embedding.Embedding> embed(dev.langchain4j.data.segment.TextSegment textSegment) {
                 return dev.langchain4j.model.output.Response.from(dev.langchain4j.data.embedding.Embedding.from(new float[1024]));
            }
            
            @Override
            public dev.langchain4j.model.output.Response<java.util.List<dev.langchain4j.data.embedding.Embedding>> embedAll(java.util.List<dev.langchain4j.data.segment.TextSegment> textSegments) {
                java.util.List<dev.langchain4j.data.embedding.Embedding> embeddings = new java.util.ArrayList<>();
                for (int i = 0; i < textSegments.size(); i++) {
                    embeddings.add(dev.langchain4j.data.embedding.Embedding.from(new float[1024]));
                }
                return dev.langchain4j.model.output.Response.from(embeddings);
            }
        };
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

    // 4. 定义 Ingestor (用于文档摄入)
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }

    // 5. 启动时加载数据
    @Bean
    public ApplicationRunner ingestDocs(EmbeddingStoreIngestor ingestor) {
        return args -> {
            // 示例：将项目中的 ADR 文档加载到向量库
            // 确保 docs/adr/0001-enterprise-ai-agent-framework-selection.md 存在
            try {
                Document document = loadDocument("docs/adr/0001-enterprise-ai-agent-framework-selection.md", new TextDocumentParser());
                
                ingestor.ingest(document);
                logger.info("已将 ADR 文档加载到长期记忆中");
            } catch (Exception e) {
                logger.warn("加载文档失败 (可能是文件不存在): {}", e.getMessage());
            }
        };
    }
}