package com.grupo3.AppProdutos.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
    String nome,
    String descricao,
    BigDecimal preco,
    String sku,
    Long categoriaId,
    Boolean ativo
) {
}

