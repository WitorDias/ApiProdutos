package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NomeUsuarioJaExisteException extends RuntimeException {
    public NomeUsuarioJaExisteException(String nome) {
        super("O nome de usuário '" + nome + "' já está em uso");
    }
}