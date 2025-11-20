package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.AutenticacaoDTO.AuthenticationRequest;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.LoginResponse;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.UsuarioAutenticadoResponse;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthenticationService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        var usuarioLogin = new UsernamePasswordAuthenticationToken(
                authenticationRequest.login(),
                authenticationRequest.senha()
        );
        var authentication = authenticationManager.authenticate(usuarioLogin);
        Usuario usuario = (Usuario) authentication.getPrincipal();

        var token = tokenService.gerarToken(usuario);

        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles()
        );
    }

    public LoginResponse refresh(String refreshToken) {
        var login = tokenService.validarToken(refreshToken);
        Usuario usuario = usuarioRepository.findByEmail(login);

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        var token = tokenService.gerarToken(usuario);

        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles()
        );
    }

    public UsuarioAutenticadoResponse obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Usuario usuario = (Usuario) authentication.getPrincipal();

        return new UsuarioAutenticadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles()
        );
    }
}
