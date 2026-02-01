package com.example.aiagent.controller;

import com.example.aiagent.agent.Assistant;
import com.example.aiagent.agent.KnowledgeBaseAssistant;
import com.example.aiagent.agent.ReActAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
public class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Assistant assistant;

    @MockBean
    private ReActAssistant reActAssistant;

    @MockBean
    private KnowledgeBaseAssistant knowledgeBaseAssistant;

    @Test
    void testChat() throws Exception {
        when(assistant.chat(anyString(), anyString())).thenReturn("Hello User");

        mockMvc.perform(get("/api/chat")
                .param("message", "Hello")
                .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello User"));
    }

    @Test
    void testChatWithTools() throws Exception {
        when(reActAssistant.chat(anyString())).thenReturn("5");

        mockMvc.perform(get("/api/chat/react")
                .param("message", "sqrt(25)"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void testChatWithKnowledge() throws Exception {
        when(knowledgeBaseAssistant.chat(anyString())).thenReturn("Because it's cool");

        mockMvc.perform(get("/api/chat/rag")
                .param("message", "Why?"))
                .andExpect(status().isOk())
                .andExpect(content().string("Because it's cool"));
    }
}
