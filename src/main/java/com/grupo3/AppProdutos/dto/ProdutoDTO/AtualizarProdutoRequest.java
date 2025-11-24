package com.grupo3.AppProdutos.dto.ProdutoDTO;

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

