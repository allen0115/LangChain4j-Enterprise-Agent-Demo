package com.example.aiagent.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("You are a helpful AI assistant in an enterprise environment.")
    String chat(@MemoryId Object memoryId, @UserMessage String userMessage);
}
