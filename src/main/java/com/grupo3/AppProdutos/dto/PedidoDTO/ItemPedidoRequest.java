package com.grupo3.AppProdutos.dto.PedidoDTO;

public record ItemPedidoRequest (
        Long produtoId,
        Integer quantidade
) {
}
