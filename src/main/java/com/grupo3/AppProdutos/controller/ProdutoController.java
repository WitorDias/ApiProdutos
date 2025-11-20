package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.ProdutoDTO.CriarProdutoRequest;
import com.grupo3.AppProdutos.dto.ProdutoDTO.ProdutoRequest;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Produto> salvarProduto(@RequestBody CriarProdutoRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(produtoService.salvarProduto(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<List<Produto>> buscarListaDeProdutos(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(produtoService.buscarListaDeProdutos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(produtoService.buscarProdutoPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequest request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(produtoService.atualizarProduto(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id){
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
