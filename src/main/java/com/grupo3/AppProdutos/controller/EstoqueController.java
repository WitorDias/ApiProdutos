package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.service.EstoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("{produtoId}")
    public ResponseEntity<Estoque> consultarEstoque(@PathVariable Long produtoId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estoqueService.buscarEstoquePorProdutoId(produtoId));
    }
}
