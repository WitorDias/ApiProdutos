package com.grupo3.AppProdutos.dto.ProdutoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequest(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 255, message = "O nome não pode ter mais que 255 caracteres")
        String nome,

        @Size(max = 2000, message = "A descrição não pode ter mais que 2000 caracteres")
        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero(message = "Preço não pode ser negativo")
        BigDecimal preco,

        @NotBlank(message = "O SKU é obrigatório")
        @Size(max = 50, message = "O SKU não pode ter mais que 50 caracteres")
        String sku,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        Boolean ativo
) {
}

