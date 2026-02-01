package com.example.aiagent.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ReActAssistant {

    @SystemMessage("You are a smart AI assistant capable of using tools. " +
            "If the user asks for a calculation or string operation, use the provided tools. " +
            "Always think step-by-step before answering.")
    String chat(String userMessage);
}
