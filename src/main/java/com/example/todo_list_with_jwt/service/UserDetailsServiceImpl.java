package com.example.todo_list_with_jwt.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.todo_list_with_jwt.dto.UsuarioLogado;
import com.example.todo_list_with_jwt.model.Usuario;
import com.example.todo_list_with_jwt.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Injeção de dependência do nosso repositório
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Vai ao banco de dados buscar o usuário
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // 2. Converte a sua entidade (Usuario) para o formato que o Spring Security entende (UserDetails)
        // return User.builder()
        //         .username(usuario.getUsername())
        //         .password(usuario.getPassword()) // Esta senha já deve estar em formato BCrypt no banco
        //         .roles(usuario.getRole())
        //         .build();
        return new UsuarioLogado(
                usuario.getUsername(),
                usuario.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_" + usuario.getRole()), // Utilitário do Spring para criar
                                                                                 // lista de roles
                usuario.getId() // <--- Passamos o ID do banco para a sessão em memória
        );
    }
}