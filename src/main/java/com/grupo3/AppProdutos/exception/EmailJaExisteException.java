package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailJaExisteException extends RuntimeException {
    public EmailJaExisteException(String email) {
        super("O email '" + email + "' já está em uso");
    }
}