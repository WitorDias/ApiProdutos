package com.grupo3.AppProdutos.dto.auditoriaDTO;

import java.math.BigDecimal;

public record ItemPedidoAuditDTO(
        Long id,
        Long pedidoId,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal precoTotal
) {
}

