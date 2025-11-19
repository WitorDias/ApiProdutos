package com.grupo3.AppProdutos.dto.ProdutoDTO;

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

