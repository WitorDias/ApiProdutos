package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Estoque", description = "Operações de consulta de estoque dos produtos")
@RestController
@RequestMapping("/v1/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Operation(
            summary = "Consultar estoque de um produto",
            description = "Retorna as informações de estoque (quantidade disponível, reservada, etc.) para um determinado produto pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Estoque.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou estoque não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado - token ausente ou inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - permissão insuficiente",
                    content = @Content
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR')")
    @GetMapping("{produtoId}")
    public ResponseEntity<Estoque> consultarEstoque(@PathVariable Long produtoId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(estoqueService.buscarEstoquePorProdutoId(produtoId));
    }
}
