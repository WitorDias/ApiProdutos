package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.CarrinhoDTO.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.CarrinhoResponse;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.FinalizarCarrinhoResponse;
import com.grupo3.AppProdutos.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/carrinhos")
@PreAuthorize("hasRole('CLIENTE')")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    public ResponseEntity<CarrinhoResponse> buscarCarrinho(@RequestParam Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.buscarCarrinhoAtivo(usuarioId));
    }

    @PostMapping("/produtos")
    public ResponseEntity<CarrinhoResponse> adicionarProduto(
            @RequestParam Long usuarioId,
            @RequestBody AdicionarItemCarrinhoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarItem(usuarioId, request));
    }

    @PutMapping("/produtos/{produtoId}")
    public ResponseEntity<CarrinhoResponse> atualizarProduto(@RequestParam Long usuarioId, @PathVariable Long produtoId, @RequestBody AtualizarItemCarrinhoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.atualizarItem(usuarioId, produtoId, request));
    }

    @DeleteMapping("/produtos/{produtoId}")
    public ResponseEntity<CarrinhoResponse> removerProduto(
            @RequestParam Long usuarioId,
            @PathVariable Long produtoId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carrinhoService.removerItem(usuarioId, produtoId));
    }

    @DeleteMapping("/limpar")
    public ResponseEntity<Void> limparCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.limparCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/finalizar")
    public ResponseEntity<FinalizarCarrinhoResponse> finalizarCarrinho(@RequestParam Long usuarioId) {
        Long pedidoId = carrinhoService.finalizarCarrinho(usuarioId);
        FinalizarCarrinhoResponse response = new FinalizarCarrinhoResponse(
                pedidoId,
                "Carrinho finalizado com sucesso! Pedido criado com status NOVO. Para confirmar o pedido e baixar o estoque, use o endpoint de atualizar status do pedido."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.cancelarCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
