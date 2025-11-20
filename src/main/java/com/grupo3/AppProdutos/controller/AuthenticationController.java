package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.AutenticacaoDTO.AuthenticationRequest;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.LoginResponse;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.RefreshTokenRequest;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.UsuarioAutenticadoResponse;
import com.grupo3.AppProdutos.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService autenticacaoService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        LoginResponse response = autenticacaoService.login(authenticationRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        LoginResponse response = autenticacaoService.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> obterUsuarioAutenticado() {
        UsuarioAutenticadoResponse response = autenticacaoService.obterUsuarioAutenticado();
        return ResponseEntity.ok(response);
    }
}

