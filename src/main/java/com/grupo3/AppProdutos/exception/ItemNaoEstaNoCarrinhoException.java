package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNaoEstaNoCarrinhoException extends RuntimeException {
    public ItemNaoEstaNoCarrinhoException() {
        super("Produto não encontrado no carrinho");
    }
}