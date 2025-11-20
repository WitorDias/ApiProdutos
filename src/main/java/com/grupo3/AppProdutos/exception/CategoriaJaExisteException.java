package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaJaExisteException extends RuntimeException {
    public CategoriaJaExisteException(String nome) {
        super("Já existe uma categoria com o nome '" + nome + "' no mesmo nível");
    }
}