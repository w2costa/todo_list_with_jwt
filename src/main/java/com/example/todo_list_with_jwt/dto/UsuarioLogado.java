package com.example.todo_list_with_jwt.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioLogado extends User {
    
    private final Long id; // Aqui guardaremos o ID do banco

    public UsuarioLogado(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, authorities); // Passa os dados padrão para o Pai
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
