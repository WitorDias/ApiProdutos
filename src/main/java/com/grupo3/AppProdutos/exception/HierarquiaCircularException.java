package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HierarquiaCircularException extends RuntimeException {
    public HierarquiaCircularException() {
        super("Hierarquia circular detectada: uma categoria não pode ser pai de si mesma");
    }
}