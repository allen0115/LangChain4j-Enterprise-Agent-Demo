package com.example.aiagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aiAgentOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AI Agent API")
                        .description("API documentation for Enterprise AI Agent Demo")
                        .version("v0.0.1"));
    }
}
