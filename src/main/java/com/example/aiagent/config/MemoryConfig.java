package com.example.aiagent.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        // 为每个 ID 创建一个独立的 ChatMemory
        // MessageWindowChatMemory(10) 表示只保留最近的 10 条消息
        return memoryId -> MessageWindowChatMemory.withMaxMessages(10);
    }
}
