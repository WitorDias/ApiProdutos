package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.MovimentoRequest;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.service.MovimentoEstoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/movimentos")
public class MovimentoEstoqueController {

    private final MovimentoEstoqueService movimentoEstoqueService;

    public MovimentoEstoqueController(MovimentoEstoqueService movimentoEstoqueService) {
        this.movimentoEstoqueService = movimentoEstoqueService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimentoEstoque> registrarEntrada(@RequestBody MovimentoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentoEstoqueService.registrarEntrada(request.produtoId(), request.quantidade()));
    }

    @PostMapping("/saida")
    public ResponseEntity<MovimentoEstoque> registrarSaida(@RequestBody MovimentoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentoEstoqueService.registrarSaida(request.produtoId(), request.quantidade()));
    }

    @GetMapping("{id}")
    public ResponseEntity<List<MovimentoEstoque>> buscarListaMovimentosPorProduto(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movimentoEstoqueService.listarMovimentosPorProdutoId(id));
    }

}
