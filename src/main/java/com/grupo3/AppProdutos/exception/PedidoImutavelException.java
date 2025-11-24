package com.grupo3.AppProdutos.exception;

import com.grupo3.AppProdutos.model.enums.StatusPedido;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PedidoImutavelException extends RuntimeException {
    public PedidoImutavelException(StatusPedido status) {
        super("Pedidos com status " + status + " não podem mais ser alterados");
    }
}