package com.grupo3.AppProdutos.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CategoriaComDependenciasExceptionCampos extends CamposPersonalizadosException{
    private String tipoDependencia;
}
