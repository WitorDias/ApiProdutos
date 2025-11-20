package com.grupo3.AppProdutos.mapper;

import com.grupo3.AppProdutos.dto.UsuarioDTO.CriarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.UsuarioResponse;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.Role;

import java.util.HashSet;
import java.util.Set;

public class UsuarioMapper {

    public static Usuario toEntity(CriarUsuarioRequest request) {
        Set<Role> roles = request.roles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.CLIENTE);
        }

        return Usuario.builder()
                .nome(request.nome())
                .senha(request.senha())
                .email(request.email())
                .roles(roles)
                .ativo(true)
                .build();
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo(),
                usuario.getRoles()
        );
    }
}
