package com.example.todo_list_with_jwt.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String mensagem;
    private LocalDateTime dataHora;

    public ErrorResponse(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }

    // Getters
    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
}