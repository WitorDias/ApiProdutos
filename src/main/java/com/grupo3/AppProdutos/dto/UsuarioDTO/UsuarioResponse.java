package com.grupo3.AppProdutos.dto.UsuarioDTO;

import com.grupo3.AppProdutos.model.enums.Role;

import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        Set<Role> roles)
{
}
