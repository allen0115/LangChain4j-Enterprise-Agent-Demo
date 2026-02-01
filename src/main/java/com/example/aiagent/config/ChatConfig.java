package com.example.aiagent.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import java.util.List;
import dev.langchain4j.data.message.ChatMessage;

@Configuration
public class ChatConfig {

    private static final Logger logger = LoggerFactory.getLogger(ChatConfig.class);

    @Value("${ai.deepseek.base-url}")
    private String baseUrl;

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.model-name}")
    private String modelName;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        logger.info("ChatConfig initializing...");
        
        if (apiKey == null || apiKey.isEmpty() || "demo".equals(apiKey) || "your-api-key-here".equals(apiKey)) {
             logger.warn("DeepSeek API Key is missing or invalid! Using Mock Chat Model.");
             return createMockChatModel();
        }

        try {
            return OpenAiChatModel.builder()
                    .baseUrl(baseUrl)
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .logRequests(true)
                    .logResponses(true)
                    .timeout(Duration.ofSeconds(60))
                    .build();
        } catch (Exception e) {
            logger.error("Failed to initialize DeepSeek: {}", e.getMessage());
            return createMockChatModel();
        }
    }

    private ChatLanguageModel createMockChatModel() {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                return Response.from(AiMessage.from("I am a Mock AI Assistant. (DeepSeek initialization failed or missing key)"));
            }
        };
    }
}
