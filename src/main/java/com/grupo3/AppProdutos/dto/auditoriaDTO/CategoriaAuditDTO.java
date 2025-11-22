package com.grupo3.AppProdutos.dto.auditoriaDTO;

import java.time.LocalDateTime;

public record CategoriaAuditDTO(
        Long id,
        String nome,
        Long parentId,
        String parentNome,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}

