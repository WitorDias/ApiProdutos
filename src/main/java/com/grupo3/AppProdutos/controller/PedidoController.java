package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.PedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoResponse;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import com.grupo3.AppProdutos.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody PedidoRequest pedidoRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido(pedidoRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.buscarPedidoPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.buscarPedidoPorUsuario(usuarioId));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<PedidoResponse> atualizarStatus(
            @PathVariable Long id,
            @PathVariable StatusPedido status
    ) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status));
    }

}

