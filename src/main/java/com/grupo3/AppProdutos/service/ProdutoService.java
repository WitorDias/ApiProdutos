package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.ProdutoDTO.CriarProdutoRequest;
import com.grupo3.AppProdutos.dto.ProdutoDTO.ProdutoRequest;
import com.grupo3.AppProdutos.exception.SkuJaExisteException;
import com.grupo3.AppProdutos.exception.ValidacaoProdutoException;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;
    private final ProdutoConsultaService produtoConsultaService;
    private final CategoriaService categoriaService;

    public ProdutoService(ProdutoRepository produtoRepository, EstoqueService estoqueService, ProdutoConsultaService produtoConsultaService, CategoriaService categoriaService) {
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
        this.produtoConsultaService = produtoConsultaService;
        this.categoriaService = categoriaService;
    }

    public List<Produto> buscarListaDeProdutos(){
        return produtoRepository.findAllByAtivoTrue();
    }

    @Transactional
    public Produto salvarProduto(CriarProdutoRequest request){
        ProdutoRequest produtoRequest = request.produto();
        validarProdutoRequest(produtoRequest);
        validarQuantidade(request.quantidade());

        produtoRepository.findBySku(produtoRequest.sku())
                .ifPresent(skuJaExiste -> {
                    throw new SkuJaExisteException();
                });

        var categoria = categoriaService.buscarCategoriaPorId(produtoRequest.categoriaId());

        Produto produto = Produto.builder()
                .nome(produtoRequest.nome())
                .descricao(produtoRequest.descricao())
                .preco(produtoRequest.preco())
                .sku(produtoRequest.sku())
                .categoria(categoria)
                .ativo(produtoRequest.ativo() != null ? produtoRequest.ativo() : true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        Produto produtoSalvo = produtoRepository.save(produto);
        estoqueService.criarEstoqueParaProduto(produtoSalvo, request.quantidade());

        return produtoSalvo;

    }

    public Produto buscarProdutoPorId(Long id){
        return produtoConsultaService.buscarProdutoPorId(id);
    }

    @Transactional
    public Produto atualizarProduto(Long id, ProdutoRequest request){
        if (request.preco() == null || request.preco().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoProdutoException("Preço não pode ser nulo ou negativo");
        }
        if (request.categoriaId() == null) {
            throw new ValidacaoProdutoException("Produto deve pertencer a uma categoria");
        }

        var produtoParaAtualizar = buscarProdutoPorId(id);
        var categoria = categoriaService.buscarCategoriaPorId(request.categoriaId());

        produtoParaAtualizar.setNome(request.nome());
        produtoParaAtualizar.setDescricao(request.descricao());
        produtoParaAtualizar.setPreco(request.preco());
        produtoParaAtualizar.setSku(request.sku());
        produtoParaAtualizar.setCategoria(categoria);
        produtoParaAtualizar.setAtivo(request.ativo() != null ? request.ativo() : produtoParaAtualizar.getAtivo());
        produtoParaAtualizar.setAtualizadoEm(LocalDateTime.now());

        return produtoRepository.save(produtoParaAtualizar);
    }

    @Transactional
    public Produto atualizarProduto(Produto produto){

        var produtoParaAtualizar = buscarProdutoPorId(produto.getId());

        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            var categoria = categoriaService.buscarCategoriaPorId(produto.getCategoria().getId());
            produtoParaAtualizar.setCategoria(categoria);
        }

        produtoParaAtualizar.setNome(produto.getNome());
        produtoParaAtualizar.setDescricao(produto.getDescricao());
        produtoParaAtualizar.setPreco(produto.getPreco());
        produtoParaAtualizar.setAtualizadoEm(LocalDateTime.now());
        produtoParaAtualizar.setSku(produto.getSku());
        produtoParaAtualizar.setAtivo(produto.getAtivo());

        validarProduto(produtoParaAtualizar);

        return produtoRepository.save(produtoParaAtualizar);

    }

    @Transactional
    public void deletarProduto(Long id){
        var produto = buscarProdutoPorId(id);
        produto.setAtivo(false);
        produto.setAtualizadoEm(LocalDateTime.now());
        produtoRepository.save(produto);
    }

    private void validarProdutoRequest(ProdutoRequest produtoRequest){
        if (produtoRequest.nome() == null || produtoRequest.nome().trim().isEmpty()) {
            throw new ValidacaoProdutoException("Nome do produto não pode ser vazio");
        }
        if (produtoRequest.preco() == null || produtoRequest.preco().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoProdutoException("Preço não pode ser nulo ou negativo");
        }
        if (produtoRequest.sku() == null || produtoRequest.sku().trim().isEmpty()) {
            throw new ValidacaoProdutoException("SKU não pode ser vazio");
        }
        if (produtoRequest.categoriaId() == null) {
            throw new ValidacaoProdutoException("Produto deve pertencer a uma categoria");
        }
    }

    private void validarProduto(Produto produto){
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ValidacaoProdutoException("Nome do produto não pode ser vazio");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoProdutoException("Preço não pode ser nulo ou negativo");
        }
        if (produto.getSku() == null || produto.getSku().trim().isEmpty()) {
            throw new ValidacaoProdutoException("SKU não pode ser vazio");
        }
        if (produto.getCategoria() == null) {
            throw new ValidacaoProdutoException("Produto deve pertencer a uma categoria");
        }

    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade <= 0){
            throw new ValidacaoProdutoException("A quantidade não pode ser nula ou menor que 1.");
        }

    }

}
