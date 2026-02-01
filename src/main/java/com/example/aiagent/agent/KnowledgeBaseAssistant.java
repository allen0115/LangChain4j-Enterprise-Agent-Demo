package com.example.aiagent.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT, // 显式模式：只注入我指定的
    chatModel = "chatLanguageModel", // 使用通用的 chat model bean
    contentRetriever = "contentRetriever", // 指定检索器
    tools = {} // 强制不使用任何工具
)
public interface KnowledgeBaseAssistant {

    @SystemMessage("You are an expert on our enterprise architecture. " +
            "You have access to a knowledge base (RAG). " +
            "Use the provided information to answer questions." +
            "If the information is not in the context, say you don't know.")
    String chat(String userMessage);
}
