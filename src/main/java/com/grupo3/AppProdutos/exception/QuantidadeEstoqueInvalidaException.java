package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QuantidadeEstoqueInvalidaException extends RuntimeException {
    public QuantidadeEstoqueInvalidaException() {
        super("A quantidade no estoque não pode ser nula ou negativa");
    }
}