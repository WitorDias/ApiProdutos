package com.grupo3.AppProdutos.dto;

import com.grupo3.AppProdutos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        StatusPedido status,
        BigDecimal valorTotal,
        List<ItemPedidoResponse> itens
) {
}
