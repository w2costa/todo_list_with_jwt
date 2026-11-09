package com.example.todo_list_with_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todo_list_with_jwt.model.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // O Spring Data JPA implementa a query automaticamente (SELECT * FROM usuarios WHERE username = ?)
    Optional<Usuario> findByUsername(String username);
}