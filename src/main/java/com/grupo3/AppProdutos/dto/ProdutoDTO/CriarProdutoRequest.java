package com.grupo3.AppProdutos.dto.ProdutoDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CriarProdutoRequest(
        @Valid
        ProdutoRequest produto,

        @NotNull(message = "A quantidade inicial no estoque é obrigatória")
        Integer quantidade
) {
}

