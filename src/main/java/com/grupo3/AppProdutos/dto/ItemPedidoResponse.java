package com.grupo3.AppProdutos.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal precoTotal
) {
}
