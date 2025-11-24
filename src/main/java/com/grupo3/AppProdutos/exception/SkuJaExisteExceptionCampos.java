package com.grupo3.AppProdutos.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SkuJaExisteExceptionCampos extends CamposPersonalizadosException{
    private String sku;
}
