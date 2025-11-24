package com.grupo3.AppProdutos.mapper;

import com.grupo3.AppProdutos.dto.ProdutoDTO.ProdutoResponse;
import com.grupo3.AppProdutos.model.Produto;

import java.util.List;
import java.util.stream.Collectors;

public class ProdutoMapper {

    public static ProdutoResponse toResponse(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getSku(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getAtivo(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }

    public static List<ProdutoResponse> toResponseList(List<Produto> produtos) {
        if (produtos == null) {
            return List.of();
        }
        return produtos.stream()
                .map(ProdutoMapper::toResponse)
                .collect(Collectors.toList());
    }
}