package com.grupo3.AppProdutos.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionCampos extends CamposPersonalizadosException{

    private final String campos;
    private final String camposMensagem;
}