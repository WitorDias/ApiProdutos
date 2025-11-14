package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.MovimentoRequest;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.service.MovimentoEstoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/estoque")
public class EstoqueController {

    private final MovimentoEstoqueService movimentoEstoqueService;

    public EstoqueController(MovimentoEstoqueService movimentoEstoqueService) {
        this.movimentoEstoqueService = movimentoEstoqueService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimentoEstoque> registrarEntrada(@RequestBody MovimentoRequest request) {
        MovimentoEstoque movimento = movimentoEstoqueService.registrarEntrada(
                request.produtoId(), request.quantidade());
        return new ResponseEntity<>(movimento, HttpStatus.CREATED);
    }

    @PostMapping("/saida")
    public ResponseEntity<MovimentoEstoque> registrarSaida(@RequestBody MovimentoRequest request) {
        MovimentoEstoque movimento = movimentoEstoqueService.registrarSaida(
                request.produtoId(), request.quantidade());
        return new ResponseEntity<>(movimento, HttpStatus.CREATED);
    }

}
