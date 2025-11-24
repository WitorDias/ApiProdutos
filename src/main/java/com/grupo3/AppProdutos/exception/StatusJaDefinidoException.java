package com.grupo3.AppProdutos.exception;

import com.grupo3.AppProdutos.model.enums.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StatusJaDefinidoException extends RuntimeException {
    public StatusJaDefinidoException(StatusPedido status) {
        super("O pedido já está com o status " + status);
    }
}