package com.grupo3.AppProdutos.dto;

import java.math.BigDecimal;

public record ItemCarrinhoResponse(
    Long id,
    Long produtoId,
    String produtoNome,
    Integer quantidade,
    BigDecimal capturaPreco,
    BigDecimal subtotal
) {
}

