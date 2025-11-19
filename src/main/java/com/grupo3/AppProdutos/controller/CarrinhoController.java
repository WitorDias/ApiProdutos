package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoResponse;
import com.grupo3.AppProdutos.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/carrinhos")
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
    public ResponseEntity<Void> finalizarCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.finalizarCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarCarrinho(@RequestParam Long usuarioId) {
        carrinhoService.cancelarCarrinho(usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

