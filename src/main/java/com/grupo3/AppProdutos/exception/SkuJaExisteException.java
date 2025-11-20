package com.grupo3.AppProdutos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SkuJaExisteException extends RuntimeException {
    public SkuJaExisteException() {
        super("SKU já está em uso por outro produto");
    }
    public SkuJaExisteException(String sku) {
        super("SKU '" + sku + "' já está em uso por outro produto");
    }
}