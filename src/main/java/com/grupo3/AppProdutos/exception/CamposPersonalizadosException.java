package com.grupo3.AppProdutos.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class CamposPersonalizadosException {

    protected LocalDateTime timestamp;
    protected int status;
    protected String erroDetalhes;
    protected String notaParaDesenvolvedor;
    protected String mensagem;

}
