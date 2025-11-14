package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto){
        return ResponseEntity.ok().body(produtoService.salvarProduto(produto));

    }

    @GetMapping
    public ResponseEntity<List<Produto>> buscarListaDeProdutos(){
        return ResponseEntity.ok().body(produtoService.buscarListaDeProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id){
        return ResponseEntity.ok().body(produtoService.buscarProdutoPorId(id));
    }

    @PutMapping
    public ResponseEntity<Void> atualizarProduto(@RequestBody Produto produto){
        produtoService.atualizarProduto(produto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id){
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
