package com.grupo3.AppProdutos.exception;

import com.grupo3.AppProdutos.model.enums.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(StatusPedido atual, StatusPedido novo) {
        super("Transição inválida: " + atual + " → " + novo);
    }
}