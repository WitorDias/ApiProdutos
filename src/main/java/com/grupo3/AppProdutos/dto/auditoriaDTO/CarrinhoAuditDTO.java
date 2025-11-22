package com.grupo3.AppProdutos.dto.auditoriaDTO;

import com.grupo3.AppProdutos.model.enums.StatusCarrinho;

import java.time.LocalDateTime;
import java.util.List;

public record CarrinhoAuditDTO(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        StatusCarrinho statusCarrinho,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        List<ItemCarrinhoAuditDTO> itens
) {
}

