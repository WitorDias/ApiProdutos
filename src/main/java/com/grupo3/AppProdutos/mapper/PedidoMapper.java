package com.grupo3.AppProdutos.mapper;

import com.grupo3.AppProdutos.dto.PedidoDTO.ItemPedidoResponse;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoResponse;
import com.grupo3.AppProdutos.model.ItemPedido;
import com.grupo3.AppProdutos.model.Pedido;

import java.util.stream.Collectors;

public class PedidoMapper {

    public static PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                pedido.getItens()
                        .stream()
                        .map(PedidoMapper::toItemResponse)
                        .collect(Collectors.toList())
        );
    }

    private static ItemPedidoResponse toItemResponse(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getProduto().getId(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getPrecoTotal()
        );
    }
}
