package com.grupo3.AppProdutos.dto;

import com.grupo3.AppProdutos.model.enums.StatusCarrinho;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoResponse(
    Long id,
    Long usuarioId,
    StatusCarrinho status,
    List<ItemCarrinhoResponse> itens,
    BigDecimal total
) {
}

