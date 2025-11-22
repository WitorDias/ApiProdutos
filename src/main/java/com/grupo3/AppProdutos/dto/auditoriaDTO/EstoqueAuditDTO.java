package com.grupo3.AppProdutos.dto.auditoriaDTO;

import java.time.LocalDateTime;

public record EstoqueAuditDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}


