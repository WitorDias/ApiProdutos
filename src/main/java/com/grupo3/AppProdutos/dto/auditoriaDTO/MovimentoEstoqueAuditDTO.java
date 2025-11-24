package com.grupo3.AppProdutos.dto.auditoriaDTO;

import com.grupo3.AppProdutos.model.enums.TipoMovimento;

import java.time.LocalDateTime;

public record MovimentoEstoqueAuditDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        TipoMovimento tipoMovimento,
        LocalDateTime criadoEm
) {
}

