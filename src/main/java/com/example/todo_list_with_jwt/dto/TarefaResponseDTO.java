package com.example.todo_list_with_jwt.dto;

import java.time.LocalDateTime;

import com.example.todo_list_with_jwt.model.Tarefa;

// TarefaResponseDTO.java
public class TarefaResponseDTO {
    public Long id;
    public String texto;
    public LocalDateTime dataCriacao;
    
    // Construtor que recebe a entidade
    public TarefaResponseDTO(Tarefa t) {
        this.id = t.getId();
        this.texto = t.getTexto();
        this.dataCriacao = t.getCreatedAt();
    }
}