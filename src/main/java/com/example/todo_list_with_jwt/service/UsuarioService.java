package com.example.todo_list_with_jwt.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo_list_with_jwt.exception.RegraNegocioException;
import com.example.todo_list_with_jwt.model.Usuario;
import com.example.todo_list_with_jwt.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; // Injetamos o encoder do SecurityConfig

    // Injeção de dependência via construtor (Melhor prática)
    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante que tudo acontece numa transação de banco
    public Usuario cadastrarNovoUsuario(Usuario usuario) {
        
        // REGRA 1: Validar duplicidade
        if (repository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RegraNegocioException("O login '" + usuario.getUsername() + "' já está em uso.");
        }

        // REGRA 2: Criptografar a senha
        // Nunca salvamos a senha pura. O encoder vem lá do SecurityConfig.
        String senhaCriptografada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(senhaCriptografada);

        // REGRA 3: Definir padrão (se necessário)
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER"); // Todo mundo nasce como USER padrão
        }

        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado com ID: " + id));
    }

    public Usuario buscarPorUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado com username: " + username));
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        // Verifica se o usuário existe
        Usuario usuarioExistente = buscarPorId(usuario.getId());

        // Atualiza os campos permitidos
        usuarioExistente.setUsername(usuario.getUsername());
        // Se a senha foi alterada, recriptografa
        if (!usuario.getPassword().equals(usuarioExistente.getPassword())) {
            String senhaCriptografada = passwordEncoder.encode(usuario.getPassword());
            usuarioExistente.setPassword(senhaCriptografada);
        }
        usuarioExistente.setRole(usuario.getRole());

        return repository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);
    }
}
