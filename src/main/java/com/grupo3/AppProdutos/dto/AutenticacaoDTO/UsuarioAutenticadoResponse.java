package com.grupo3.AppProdutos.dto.AutenticacaoDTO;

import com.grupo3.AppProdutos.model.enums.Role;

import java.util.Set;

public record UsuarioAutenticadoResponse(
        Long id,
        String nome,
        String email,
        Set<Role> papeis
) {
}

