package com.example.todo_list_with_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // Construtor injetando o filtro que criaremos para validar o token
    public SecurityConfig(JwtRequestFilter jwtRequestFilter,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desativa o CSRF, pois a autenticação via JWT enviada no cabeçalho (Header)
                // já mitiga naturalmente os ataques de CSRF que dependem de Cookies automáticos
                // do navegador.
                .csrf(csrf -> csrf.disable())

                // 2. Configuração de rotas públicas e protegidas
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso público a qualquer rota que comece com /api/auth/ (ex: login,
                        // cadastro)
                        .requestMatchers("/api/auth/**").permitAll()
                        // LIBERAÇÃO DO H2: Permite acesso total à URL do console
                        .requestMatchers("/h2-console/**").permitAll()
                        // LIBERAÇÃO DO SWAGGER: Permite acesso público à documentação da API
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Se o seu frontend (arquivos HTML, JS, CSS) for servido pelo próprio Spring
                        // Boot, você precisará liberar as rotas estáticas:
                        .requestMatchers("/",
                                "/index.html",
                                "/js/**",
                                "/css/**",
                                "/favicon.ico",
                                "/.well-known/**",
                                "/images/**")
                        .permitAll()
                        // Qualquer outra requisição à API precisará estar autenticada
                        .anyRequest().authenticated())
                // Diz ao Spring Security para usar o nosso EntryPoint quando a autenticação
                // falhar
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // LIBERAÇÃO DE FRAMES: Permite que o console do H2 seja renderizado no
                // navegador
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))
                // 3. Define a política de sessão como estritamente STATELESS.
                // O Spring Security não guardará nenhuma sessão JSESSIONID na memória do
                // servidor.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Diz ao Spring para rodar o nosso filtro JWT *antes* do filtro padrão de
                // usuário/senha.
                // Isso garante que se o token for válido, o Spring já saberá quem é o usuário
                // antes de barrá-lo.
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean crucial para que o seu Controller de Autenticação possa usar o Spring
    // para checar se a senha e usuário digitados no formulário de login batem com o
    // banco.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configuração do codificador de senhas (usando BCrypt, padrão de mercado)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
