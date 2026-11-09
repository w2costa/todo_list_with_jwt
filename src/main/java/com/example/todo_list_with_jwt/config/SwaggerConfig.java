package com.example.todo_list_with_jwt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define o nome do esquema de segurança (pode ser qualquer nome, usaremos "bearerAuth")
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Tarefas (Todo API)")
                        .version("1.0")
                        .description("Documentação da API RESTful com autenticação JWT"))
                // 1. Adiciona o botão "Authorize" globalmente
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 2. Configura como o botão "Authorize" vai funcionar (Bearer Token / JWT)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
