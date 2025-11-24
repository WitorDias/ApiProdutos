package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.EstoqueDTO.MovimentoRequest;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.service.MovimentoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Movimentos de Estoque", description = "Operações relacionadas ao controle de entrada, saída e histórico de movimentos de estoque dos produtos")
@RestController
@RequestMapping("v1/movimentos")
public class MovimentoEstoqueController {

    private final MovimentoEstoqueService movimentoEstoqueService;

    public MovimentoEstoqueController(MovimentoEstoqueService movimentoEstoqueService) {
        this.movimentoEstoqueService = movimentoEstoqueService;
    }

    @Operation(
            summary = "Registrar entrada de estoque",
            description = "Registra uma movimentação do tipo ENTRADA para um produto específico, aumentando a quantidade disponível em estoque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada registrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimentoEstoque.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou quantidade menor ou igual a zero", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto ou estoque não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - requer permissão adequada (ex: ADMIN ou VENDEDOR)", content = @Content)
    })
    @PostMapping("/entrada")
    public ResponseEntity<MovimentoEstoque> registrarEntrada(@RequestBody MovimentoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentoEstoqueService.registrarEntrada(request.produtoId(), request.quantidade()));
    }

    @Operation(
            summary = "Registrar saída de estoque",
            description = "Registra uma movimentação do tipo SAÍDA para um produto específico, diminuindo a quantidade disponível em estoque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saída registrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimentoEstoque.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou quantidade menor ou igual a zero", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto ou estoque não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente para a quantidade solicitada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - requer permissão adequada (ex: ADMIN ou VENDEDOR)", content = @Content)
    })
    @PostMapping("/saida")
    public ResponseEntity<MovimentoEstoque> registrarSaida(@RequestBody MovimentoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movimentoEstoqueService.registrarSaida(request.produtoId(), request.quantidade()));
    }

    @Operation(
            summary = "Listar histórico de movimentos por produto",
            description = "Retorna todas as movimentações de entrada e saída realizadas para um produto específico, ordenadas cronologicamente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimentoEstoque.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - requer permissão adequada (ex: ADMIN ou VENDEDOR)", content = @Content)
    })
    @GetMapping("{id}")
    public ResponseEntity<List<MovimentoEstoque>> buscarListaMovimentosPorProduto(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movimentoEstoqueService.listarMovimentosPorProdutoId(id));
    }

}
