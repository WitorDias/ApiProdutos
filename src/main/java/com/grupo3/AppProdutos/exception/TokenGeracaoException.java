package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenGeracaoException extends RuntimeException {
    public TokenGeracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

