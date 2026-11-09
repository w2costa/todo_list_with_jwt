package com.example.todo_list_with_jwt.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String texto;

    // --- RELACIONAMENTO ---
    // @ManyToOne: Muitas tarefas pertencem a UM usuário.
    // @JoinColumn: Define o nome da coluna no banco (usuario_id).
    // nullable = false: Uma tarefa OBRIGATORIAMENTE tem um dono.
    @ManyToOne 
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

// --- DATAS AUTOMÁTICAS ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Antes de salvar pela primeira vez
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Antes de atualizar qualquer dado
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
