package com.grupo3.AppProdutos.controller;

import com.grupo3.AppProdutos.dto.CategoriaDTO.AtualizarCategoriaRequest;
import com.grupo3.AppProdutos.dto.CategoriaDTO.CriarCategoriaRequest;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorias", description = "Operações relacionadas a Categorias")
@RestController
@RequestMapping("v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Categoria já existe", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> salvarCategoria(@RequestBody @Valid CriarCategoriaRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoriaService.salvarCategoria(request));
    }

    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<List<Categoria>> buscarListaDeCategorias(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoriaService.buscarListaDeCategorias());
    }

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoriaService.buscarCategoriaPorId(id));
    }

    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - categoria já existe com esse nome", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarCategoria(@PathVariable Long id, @RequestBody @Valid AtualizarCategoriaRequest request){
        categoriaService.atualizarCategoria(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Deletar categoria", description = "Remove uma categoria do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Categoria possui dependências (produtos ou subcategorias)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMIN", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id){
        categoriaService.deletarCategoria(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
