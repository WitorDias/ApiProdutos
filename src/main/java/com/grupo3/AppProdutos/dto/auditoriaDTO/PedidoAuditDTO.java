package com.grupo3.AppProdutos.dto.auditoriaDTO;

import com.grupo3.AppProdutos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoAuditDTO(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        BigDecimal valorTotal,
        StatusPedido status,
        LocalDateTime criadoEm,
        List<ItemPedidoAuditDTO> itens
) {
}


