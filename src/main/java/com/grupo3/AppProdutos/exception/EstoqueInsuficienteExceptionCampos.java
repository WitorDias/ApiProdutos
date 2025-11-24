package com.grupo3.AppProdutos.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class EstoqueInsuficienteExceptionCampos extends CamposPersonalizadosException{
    private Integer disponivel;
    private Integer solicitado;
}
