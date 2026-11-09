package com.example.todo_list_with_jwt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.example.todo_list_with_jwt.security.JwtUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // O Spring injeta automaticamente o UserDetailsService da sua aplicação e o utilitário JWT
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. Obtém o cabeçalho 'Authorization' da requisição HTTP
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. O padrão de mercado diz que o token JWT deve começar com o prefixo "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove o "Bearer " para isolar apenas o token
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Aqui você pode capturar logs se o token estiver expirado ou corrompido
                logger.warn("Token JWT inválido, expirado ou malformado.");
            }
        }

        // 3. Se extraiu o username com sucesso e o usuário ainda não está autenticado no contexto atual
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carrega os dados do usuário a partir do banco de dados (ou memória, dependendo do seu projeto)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Valida se o token é legítimo (assinatura correta e dentro da validade)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Cria o objeto de autenticação padrão do Spring Security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Salva o usuário no contexto de segurança do Spring. 
                // A partir deste momento, o Spring sabe quem está acessando e permite a requisição prosseguir.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 6. Continua o fluxo normal da requisição (passando para os próximos filtros ou para o Controller)
        chain.doFilter(request, response);
    }
}
