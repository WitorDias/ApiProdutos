package com.grupo3.AppProdutos.handler;

import com.grupo3.AppProdutos.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CamposPersonalizadosException> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String campos = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(","));
        String camposMensagem = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                ValidationExceptionCampos.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .erroDetalhes("Bad Request Exception. Invalid fields.")
                        .notaParaDesenvolvedor(exception.getClass().getName())
                        .mensagem("Check if some fields may have invalid inputs.")
                        .campos(campos)
                        .camposMensagem(camposMensagem)
                        .build(), HttpStatus.BAD_REQUEST);
    }

    // 400 - Validação manual no service
    @ExceptionHandler(ValidacaoProdutoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleValidacaoProduto(ValidacaoProdutoException ex) {
        ValidacaoProdutoExceptionCampos response = ValidacaoProdutoExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Erro de validação do produto")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 404 - Produto não encontrado
    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        ProdutoNaoEncontradoExceptionCampos response = ProdutoNaoEncontradoExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Recurso não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 404 - Categoria não encontrada
    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCategoriaNaoEncontrada(CategoriaNaoEncontradaException ex) {
        CategoriaNaoEncontradaExceptionCampos response = CategoriaNaoEncontradaExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Recurso não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 409 - SKU já existe
    @ExceptionHandler(SkuJaExisteException.class)
    public ResponseEntity<CamposPersonalizadosException> handleSkuJaExiste(SkuJaExisteException ex) {
        SkuJaExisteExceptionCampos response = SkuJaExisteExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erroDetalhes("Conflito de recurso")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .sku(ex.getMessage().contains("'") ? ex.getMessage().split("'")[1] : null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
