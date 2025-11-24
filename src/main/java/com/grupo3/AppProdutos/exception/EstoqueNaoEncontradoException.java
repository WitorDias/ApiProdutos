package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EstoqueNaoEncontradoException extends RuntimeException {
    public EstoqueNaoEncontradoException(Long produtoId) {
        super("Estoque não encontrado para o produto com ID: " + produtoId);
    }
}