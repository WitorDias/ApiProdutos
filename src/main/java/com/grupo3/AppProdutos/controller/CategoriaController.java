package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.CategoriaDTO.AtualizarCategoriaRequest;
import com.grupo3.AppProdutos.dto.CategoriaDTO.CriarCategoriaRequest;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Categoria> salvarCategoria(@RequestBody CriarCategoriaRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoriaService.salvarCategoria(request));
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> buscarListaDeCategorias(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoriaService.buscarListaDeCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoriaService.buscarCategoriaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarCategoria(@PathVariable Long id, @RequestBody AtualizarCategoriaRequest request){
        categoriaService.atualizarCategoria(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id){
        categoriaService.deletarCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
