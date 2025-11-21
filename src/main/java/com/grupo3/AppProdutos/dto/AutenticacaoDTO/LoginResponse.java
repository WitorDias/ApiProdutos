package com.grupo3.AppProdutos.dto.AutenticacaoDTO;

import com.grupo3.AppProdutos.model.enums.Role;

import java.util.Set;

public record LoginResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nome,
        String email,
        Set<Role> papeis
) {
    public LoginResponse(String token, Long usuarioId, String nome, String email, Set<Role> papeis) {
        this(token, "Bearer", usuarioId, nome, email, papeis);
    }
}

