package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(int disponivel, int solicitado) {
        super(String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d", disponivel, solicitado));
    }
}