package com.grupo3.AppProdutos.dto.CarrinhoDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdicionarItemCarrinhoRequest(
    @NotNull(message = "O ID do produto é obrigatório")
    Long produtoId,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Integer quantidade
) {
}

