package com.grupo3.AppProdutos.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SkuJaExisteExceptionCampos extends CamposPersonalizadosException{
    private String sku;
}
