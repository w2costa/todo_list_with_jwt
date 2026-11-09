package com.example.todo_list_with_jwt.config;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.todo_list_with_jwt.dto.ErrorResponse;
import com.example.todo_list_with_jwt.exception.RegraNegocioException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Trata nossa exceção de Regra de Negócio (ex: usuário duplicado)
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraNegocio(RegraNegocioException ex) {
        ErrorResponse erro = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 2. Trata exceção de "Elemento não encontrado" (ex: buscar Tarefa por ID que
    // não existe)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        // Logar o erro no servidor para o dev ver (ex.printStackTrace())
        ex.printStackTrace();

        // Retornar mensagem genérica para o usuário (Segurança: não mostre detalhes
        // técnicos)
        ErrorResponse erro = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno no servidor. Contate o suporte.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        // Retorna HTTP 401 com uma mensagem amigável
        ErrorResponse erro = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Erro: Nome de usuário ou senha incorretos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // Opcional, mas recomendado: capturar se o usuário não for encontrado no banco
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        // Por segurança, é boa prática dar a mesma mensagem genérica para não revelar
        // a um atacante se o usuário existe ou não no banco de dados.
        ErrorResponse erro = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Erro: Nome de usuário ou senha incorretos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse erro = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado. Você não tem permissão para acessar este recurso.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // 3. Trata erros genéricos inesperados (NullPointer, Banco fora do ar, etc)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptionGenerica(Exception ex) {
        // Logar o erro no servidor para o dev ver (ex.printStackTrace())
        ex.printStackTrace();

        // Retornar mensagem genérica para o usuário (Segurança: não mostre detalhes
        // técnicos)
        ErrorResponse erro = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno no servidor. Contate o suporte.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}