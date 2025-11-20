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
                        .erroDetalhes("Bad Request Exception. Campos inválidos.")
                        .notaParaDesenvolvedor(exception.getClass().getName())
                        .mensagem("Verifique se há campos com informações inválidas.")
                        .campos(campos)
                        .camposMensagem(camposMensagem)
                        .build(), HttpStatus.BAD_REQUEST);
    }

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

    @ExceptionHandler(CategoriaJaExisteException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCategoriaJaExiste(CategoriaJaExisteException ex) {
        String nome = ex.getMessage().contains("'") ? ex.getMessage().split("'")[1] : null;
        CategoriaJaExisteExceptionCampos response = CategoriaJaExisteExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erroDetalhes("Conflito de categoria")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .nome(nome)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CategoriaComDependenciasException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCategoriaComDependencias(CategoriaComDependenciasException ex) {
        String tipo = ex.getMessage().contains("produtos") ? "produtos" : "subcategorias";
        CategoriaComDependenciasExceptionCampos response = CategoriaComDependenciasExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erroDetalhes("Categoria possui dependências")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .tipoDependencia(tipo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HierarquiaCircularException.class)
    public ResponseEntity<CamposPersonalizadosException> handleHierarquiaCircular(HierarquiaCircularException ex) {
        HierarquiaCircularExceptionCampos response = HierarquiaCircularExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Hierarquia inválida")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleValidacao(ValidacaoException ex) {
        ValidacaoExceptionCampos response = ValidacaoExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Erro de validação")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Recurso não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarrinhoNaoEncontradoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCarrinhoNaoEncontrado(CarrinhoNaoEncontradoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Carrinho não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNaoEstaNoCarrinhoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleItemNaoEstaNoCarrinho(ItemNaoEstaNoCarrinhoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Item não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<CamposPersonalizadosException> handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        String msg = ex.getMessage();
        int disponivel = Integer.parseInt(msg.split("Disponível: ")[1].split(",")[0]);
        int solicitado = Integer.parseInt(msg.split("Solicitado: ")[1]);

        var response = EstoqueInsuficienteExceptionCampos.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erroDetalhes("Estoque insuficiente")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .disponivel(disponivel)
                .solicitado(solicitado)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CarrinhoVazioException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCarrinhoVazio(CarrinhoVazioException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Carrinho vazio")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarrinhoAtivoJaExisteException.class)
    public ResponseEntity<CamposPersonalizadosException> handleCarrinhoAtivoJaExiste(CarrinhoAtivoJaExisteException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erroDetalhes("Conflito de estado do carrinho")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EstoqueNaoEncontradoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleEstoqueNaoEncontrado(EstoqueNaoEncontradoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Recurso não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QuantidadeEstoqueInvalidaException.class)
    public ResponseEntity<CamposPersonalizadosException> handleQuantidadeInvalida(QuantidadeEstoqueInvalidaException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Validação de estoque")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuantidadeInvalidaException.class)
    public ResponseEntity<CamposPersonalizadosException> handleQuantidadeInvalida(QuantidadeInvalidaException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Validação de quantidade")
                .notaParaDesenvolvedor(ex.getClass().getName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<CamposPersonalizadosException> handlePedidoNaoEncontrado(PedidoNaoEncontradoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erroDetalhes("Pedido não encontrado")
                .notaParaDesenvolvedor(ex.getClass().getSimpleName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<CamposPersonalizadosException> handleTransicaoInvalida(TransicaoStatusInvalidaException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Transição de status inválida")
                .notaParaDesenvolvedor(ex.getClass().getSimpleName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusJaDefinidoException.class)
    public ResponseEntity<CamposPersonalizadosException> handleStatusJaDefinido(StatusJaDefinidoException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Status já aplicado")
                .notaParaDesenvolvedor(ex.getClass().getSimpleName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PedidoImutavelException.class)
    public ResponseEntity<CamposPersonalizadosException> handlePedidoImutavel(PedidoImutavelException ex) {
        var response = CamposPersonalizadosException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erroDetalhes("Pedido imutável")
                .notaParaDesenvolvedor(ex.getClass().getSimpleName())
                .mensagem(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
