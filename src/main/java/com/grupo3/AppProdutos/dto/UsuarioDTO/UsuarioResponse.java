package com.grupo3.AppProdutos.dto.UsuarioDTO;

import com.grupo3.AppProdutos.model.enums.Papel;

import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo)
{
}
