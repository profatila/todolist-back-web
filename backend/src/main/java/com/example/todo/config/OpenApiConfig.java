package com.example.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo List API")
                        .version("1.0.0")
                        .description("API RESTful pública para gerenciamento de tarefas sem autenticação")
                        .contact(new Contact()
                                .name("Suporte da Aplicação")
                                .email("suporte@example.com")));
    }
}
