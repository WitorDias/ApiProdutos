package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.CarrinhoDTO.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.CarrinhoResponse;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.FinalizarCarrinhoResponse;
import com.grupo3.AppProdutos.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrinho de Compras", description = "Operações relacionadas ao carrinho de compras do cliente")
@RestController
@RequestMapping("v1/carrinhos")
@PreAuthorize("hasRole('CLIENTE')")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @Operation(summary = "Buscar carrinho ativo", description = "Retorna o carrinho de compras ativo do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrinho retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum carrinho ativo encontrado para o usuário", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CarrinhoResponse> buscarCarrinho(@RequestParam Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.buscarCarrinhoAtivo(usuarioId));
    }

    @Operation(summary = "Adicionar produto ao carrinho", description = "Adiciona um novo item (produto + quantidade) ao carrinho ativo do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto adicionado ao carrinho com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou quantidade indisponível", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado ou carrinho não existe", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - estoque insuficiente", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @PostMapping("/produtos")
    public ResponseEntity<CarrinhoResponse> adicionarProduto(
            @RequestParam Long usuarioId,
            @RequestBody AdicionarItemCarrinhoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarItem(usuarioId, request));
    }

    @Operation(summary = "Atualizar quantidade de um produto no carrinho", description = "Altera a quantidade de um item já existente no carrinho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou quantidade indisponível", content = @Content),
            @ApiResponse(responseCode = "404", description = "Item ou carrinho não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - estoque insuficiente para a nova quantidade", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @PutMapping("/produtos/{produtoId}")
    public ResponseEntity<CarrinhoResponse> atualizarProduto(@RequestParam Long usuarioId, @PathVariable Long produtoId, @RequestBody AtualizarItemCarrinhoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.atualizarItem(usuarioId, produtoId, request));
    }

    @Operation(summary = "Remover produto do carrinho", description = "Remove completamente um item do carrinho de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto removido do carrinho com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item ou carrinho não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @DeleteMapping("/produtos/{produtoId}")
    public ResponseEntity<CarrinhoResponse> removerProduto(
            @RequestParam Long usuarioId,
            @PathVariable Long produtoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.removerItem(usuarioId, produtoId));
    }

    @Operation(summary = "Limpar carrinho", description = "Remove todos os itens do carrinho ativo do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrinho limpo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado ou já está vazio", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @DeleteMapping("/limpar")
    public ResponseEntity<Void> limparCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.limparCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Finalizar carrinho", description = "Transforma o carrinho ativo em um pedido com status NOVO. Não baixa estoque ainda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrinho finalizado e pedido criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinalizarCarrinhoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Carrinho vazio ou dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - estoque insuficiente para algum item", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @PostMapping("/finalizar")
    public ResponseEntity<FinalizarCarrinhoResponse> finalizarCarrinho(@RequestParam Long usuarioId) {
        Long pedidoId = carrinhoService.finalizarCarrinho(usuarioId);
        FinalizarCarrinhoResponse response = new FinalizarCarrinhoResponse(
                pedidoId,
                "Carrinho finalizado com sucesso! Pedido criado com status NOVO. Para confirmar o pedido e baixar o estoque, use o endpoint de atualizar status do pedido."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cancelar carrinho", description = "Descarta o carrinho ativo sem criar pedido (ex: usuário desistiu da compra)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrinho cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas CLIENTE", content = @Content)
    })
    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.cancelarCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
