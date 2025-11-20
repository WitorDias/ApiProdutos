package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaComDependenciasException extends RuntimeException {
    public CategoriaComDependenciasException(String mensagem) {
        super(mensagem);
    }
}