package com.example.todo_list_with_jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtUtil {

    // IMPORTANTE: Em produção, NUNCA deixe a chave "hardcoded". Use application.properties ou variáveis de ambiente.
    // A chave precisa ter pelo menos 256 bits (32 caracteres) para o algoritmo HS256.
    private static final String SECRET_KEY_STRING = "ExemploDeChaveSuperSecretaParaSuaAulaDeSpring2026!";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Tempo de expiração do token em milissegundos (Exemplo: 10 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // 1. Extrair o Username (Subject) do token
    public String extractUsername(String token) {
        return extractClaim(token, claims -> Objects.requireNonNull(claims).getSubject());
    }

    // 2. Extrair a data de expiração do token
    public Date extractExpiration(String token) {
        return extractClaim(token, claims -> Objects.requireNonNull(claims).getExpiration());
    }

    // Método genérico para extrair qualquer informação (Claim) do token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Descriptografa o token usando a chave secreta para ler o conteúdo (Payload)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica se o token já passou da data de validade
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 3. Gerar um novo token para o usuário (chamado logo após o login de sucesso)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Você pode adicionar mais informações (claims) aqui se desejar, como roles/perfil.
        return createToken(claims, userDetails.getUsername());
    }

    // Constrói o JWT com cabeçalho, payload e assinatura
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // O dono do token (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Data de validade
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Assinatura digital
                .compact();
    }

    // 4. Validar se o token pertence ao usuário e se ainda está dentro da validade
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
