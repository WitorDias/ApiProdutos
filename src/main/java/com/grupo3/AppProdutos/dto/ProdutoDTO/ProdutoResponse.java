package com.grupo3.AppProdutos.dto.ProdutoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse (

        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String sku,
        Long categoriaId,
        String categoriaNome,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

){
}
