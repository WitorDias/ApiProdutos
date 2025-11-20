package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CarrinhoAtivoJaExisteException extends RuntimeException {
    public CarrinhoAtivoJaExisteException() {
        super("O usuário já possui um carrinho ativo");
    }
}