package com.grupo3.AppProdutos.dto;

public record ItemPedidoRequest (
        Long produtoId,
        Integer quantidade
) {
}
