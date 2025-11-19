package com.grupo3.AppProdutos.mapper;

import com.grupo3.AppProdutos.dto.UsuarioDTO.CriarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.UsuarioResponse;
import com.grupo3.AppProdutos.model.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .nome(request.nome())
                .senha(request.senha())
                .email(request.email())
                .ativo(true)
                .build();
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo()
        );
    }
}
