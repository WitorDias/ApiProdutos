package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.UsuarioDTO.AtualizarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.CriarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.UsuarioResponse;
import com.grupo3.AppProdutos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest criarUsuarioRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.criarUsuario(criarUsuarioRequest));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody AtualizarUsuarioRequest atualizarUsuarioRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.atualizarUsuario(id, atualizarUsuarioRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id){
        usuarioService.deletarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
