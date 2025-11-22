package com.grupo3.AppProdutos.dto.auditoriaDTO;

import java.math.BigDecimal;

public record ItemCarrinhoAuditDTO(
        Long id,
        Long carrinhoId,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal capturaPreco
) {
}

