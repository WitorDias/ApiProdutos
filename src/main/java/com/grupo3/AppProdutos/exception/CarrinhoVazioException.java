package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CarrinhoVazioException extends RuntimeException {
    public CarrinhoVazioException() {
        super("Não é possível finalizar um carrinho vazio");
    }
}