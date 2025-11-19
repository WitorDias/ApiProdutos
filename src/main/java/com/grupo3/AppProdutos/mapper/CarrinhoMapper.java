package com.grupo3.AppProdutos.mapper;

import com.grupo3.AppProdutos.dto.CarrinhoResponse;
import com.grupo3.AppProdutos.dto.ItemCarrinhoResponse;
import com.grupo3.AppProdutos.model.Carrinho;
import com.grupo3.AppProdutos.model.ItemCarrinho;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarrinhoMapper {

    public CarrinhoResponse toResponse(Carrinho carrinho) {
        List<ItemCarrinhoResponse> itensResponse = carrinho.getItens().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = itensResponse.stream()
                .map(ItemCarrinhoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarrinhoResponse(
                carrinho.getId(),
                carrinho.getUsuario().getId(),
                carrinho.getStatusCarrinho(),
                itensResponse,
                total
        );
    }

    public ItemCarrinhoResponse toItemResponse(ItemCarrinho item) {
        BigDecimal subtotal = item.getCapturaPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new ItemCarrinhoResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getCapturaPreco(),
                subtotal
        );
    }
}

