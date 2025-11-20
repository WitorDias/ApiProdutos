package com.grupo3.AppProdutos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.grupo3.AppProdutos.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("AppProdutos API")
                    .withSubject(usuario.getId().toString())
                    .withClaim("nome", usuario.getNome())
                    .withClaim("login", usuario.getEmail())
                    .withExpiresAt(geraDataDeExpiracao())
                    .sign(algorithm);

            return token;

        }catch (JWTCreationException e ){

            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("AppProdutos API")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTCreationException e) {
            return null;
        }
    }

    private Instant geraDataDeExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}