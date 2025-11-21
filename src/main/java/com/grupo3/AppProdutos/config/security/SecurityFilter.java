package com.grupo3.AppProdutos.config.security;

import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import com.grupo3.AppProdutos.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = recoverToken(request);

            if (token != null) {
                try {
                    String subject = tokenService.validarToken(token);

                    if (subject != null) {
                        Long usuarioId = Long.parseLong(subject);
                        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

                        if (usuario != null) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            usuario,
                                            null,
                                            usuario.getAuthorities()
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Erro ao validar token: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
