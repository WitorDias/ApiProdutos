package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoResponse;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import com.grupo3.AppProdutos.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody PedidoRequest pedidorequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido(pedidorequest));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.buscarPedidoPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.buscarPedidoPorUsuario(usuarioId));
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.atualizarStatus(id, StatusPedido.CONFIRMADO));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.atualizarStatus(id, StatusPedido.CANCELADO));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.atualizarStatus(id, StatusPedido.FINALIZADO));
    }

}
