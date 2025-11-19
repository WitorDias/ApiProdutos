package com.grupo3.AppProdutos.dto;

import java.util.List;

public record PedidoRequest(
        Long usuarioId,
        List<ItemPedidoRequest> itens
) {
}
