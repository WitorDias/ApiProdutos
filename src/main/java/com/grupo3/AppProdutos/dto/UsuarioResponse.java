package com.grupo3.AppProdutos.dto;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo)
{
}
