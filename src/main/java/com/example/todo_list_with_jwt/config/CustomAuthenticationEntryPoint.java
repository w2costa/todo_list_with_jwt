package com.example.todo_list_with_jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import com.example.todo_list_with_jwt.dto.ErrorResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response, 
                         AuthenticationException authException) throws IOException, ServletException {
        
        // 1. Configura o cabeçalho da resposta para JSON e o status para 401 (Unauthorized)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 2. Instancia a sua classe ErrorResponse (ajuste os parâmetros conforme o construtor que você criou)
        // Usamos 401 aqui porque o usuário não está autenticado. 
        // 403 seria se ele estivesse logado, mas não tivesse permissão (ex: apenas ADMIN).
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED, 
                "Acesso negado. É necessário fornecer um token JWT válido."
        );

        // 1. Criamos o ObjectMapper manualmente
        ObjectMapper mapper = new ObjectMapper();
        
        // 2. Ensinamos o Jackson a lidar com o LocalDateTime do Java 8+
        mapper.registerModule(new JavaTimeModule());
        
        // 3. Desativamos o formato de timestamp nativo para que a data saia no formato String ISO 8601 legível (ex: "2026-06-10T17:54:47")
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 4. Escrevemos a resposta JSON
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}