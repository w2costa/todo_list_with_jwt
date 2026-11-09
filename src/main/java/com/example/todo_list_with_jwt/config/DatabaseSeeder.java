package com.example.todo_list_with_jwt.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todo_list_with_jwt.model.Usuario;
import com.example.todo_list_with_jwt.repository.UsuarioRepository;


@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Verifica se o usuário 'admin' já existe (boa prática)
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                
                // 2. Cria a nova entidade
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                
                // 3. O DETALHE CRUCIAL: Criptografar a senha em texto puro ("123456") 
                // usando o codificador configurado (BCrypt) antes de salvar!
                admin.setPassword(passwordEncoder.encode("123456"));

                admin.setRole("ADMIN");
                
                // 4. Salva no banco H2
                usuarioRepository.save(admin);
                
                // Apenas um log no console para avisar que deu certo
                System.out.println("========== Banco de dados inicializado ==========");
                System.out.println("Usuário padrão criado: admin / 123456");
                System.out.println("=================================================");
            }
        };
    }
}