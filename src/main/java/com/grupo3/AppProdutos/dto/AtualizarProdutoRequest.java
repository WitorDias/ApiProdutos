package com.grupo3.AppProdutos.dto;

import java.math.BigDecimal;

public record AtualizarProdutoRequest(
    String nome,
    String descricao,
    BigDecimal preco,
    String sku,
    Long categoriaId,
    Boolean ativo
) {
}

