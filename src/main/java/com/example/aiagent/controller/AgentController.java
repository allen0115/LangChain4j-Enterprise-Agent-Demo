package com.example.aiagent.controller;

import com.example.aiagent.agent.Assistant;
import com.example.aiagent.agent.KnowledgeBaseAssistant;
import com.example.aiagent.agent.ReActAssistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/chat")
public class AgentController {

    private final Assistant assistant;
    private final ReActAssistant reActAssistant;
    private final KnowledgeBaseAssistant knowledgeBaseAssistant;

    public AgentController(Assistant assistant, ReActAssistant reActAssistant, KnowledgeBaseAssistant knowledgeBaseAssistant) {
        this.assistant = assistant;
        this.reActAssistant = reActAssistant;
        this.knowledgeBaseAssistant = knowledgeBaseAssistant;
    }

    @GetMapping
    public String chat(
            @RequestParam(value = "message", defaultValue = "Hello") String message,
            @RequestParam(value = "userId", defaultValue = "user1") String userId
    ) {
        return assistant.chat(userId, message);
    }

    @GetMapping("/react")
    public String chatWithTools(@RequestParam(value = "message", defaultValue = "What is the square root of 25?") String message) {
        return reActAssistant.chat(message);
    }

    @GetMapping("/rag")
    public String chatWithKnowledge(@RequestParam(value = "message", defaultValue = "Why LangChain4j?") String message) {
        return knowledgeBaseAssistant.chat(message);
    }
}
