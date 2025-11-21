package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.AutenticacaoDTO.AuthenticationRequest;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.LoginResponse;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.RefreshTokenRequest;
import com.grupo3.AppProdutos.dto.AutenticacaoDTO.UsuarioAutenticadoResponse;
import com.grupo3.AppProdutos.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Operações de login, refresh de token e consulta do usuário autenticado")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService autenticacaoService;

    @Operation(summary = "Realizar login", description = "Autentica o usuário com login e senha e retorna um token JWT válido")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos ou malformados", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        LoginResponse response = autenticacaoService.login(authenticationRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh de token", description = "Gera um novo token JWT a partir de um refresh token válido")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token renovado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido ou malformado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Refresh token expirado ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário associado ao token não encontrado", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        LoginResponse response = autenticacaoService.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter dados do usuário autenticado", description = "Retorna as informações do usuário atualmente autenticado via token JWT (endpoint protegido)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioAutenticadoResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado - token ausente, inválido ou expirado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> obterUsuarioAutenticado() {
        UsuarioAutenticadoResponse response = autenticacaoService.obterUsuarioAutenticado();
        return ResponseEntity.ok(response);
    }
}

