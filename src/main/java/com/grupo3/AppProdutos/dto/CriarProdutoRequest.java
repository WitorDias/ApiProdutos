package com.grupo3.AppProdutos.dto;

import com.grupo3.AppProdutos.model.Produto;

public record CriarProdutoRequest(Produto produto, Integer quantidade) {
}
