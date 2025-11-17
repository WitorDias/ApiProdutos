package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.CriarProdutoRequest;
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
    private final MovimentoEstoqueService movimentoEstoqueService;
    private final ProdutoConsultaService produtoConsultaService;

    public ProdutoService(ProdutoRepository produtoRepository, EstoqueService estoqueService, MovimentoEstoqueService movimentoEstoqueService, ProdutoConsultaService produtoConsultaService) {
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
        this.movimentoEstoqueService = movimentoEstoqueService;
        this.produtoConsultaService = produtoConsultaService;
    }

    public List<Produto> buscarListaDeProdutos(){
        return produtoRepository.findAllByAtivoTrue();
    }

    @Transactional
    public Produto salvarProduto(CriarProdutoRequest request){
        Produto produto = request.produto();
        validarProduto(request.produto());
        validarQuantidade(request.quantidade());

        if(produto.getAtivo() == null){
            produto.setAtivo(true);
        }

        produtoRepository.findBySku(produto.getSku())
                .ifPresent(skuJaExiste -> {
                    throw new IllegalArgumentException("SKU já está em uso");
                });

        produto.setCriadoEm(LocalDateTime.now());
        produto.setAtualizadoEm(LocalDateTime.now());

        Produto produtoSalvo = produtoRepository.save(produto);
        estoqueService.criarEstoqueParaProduto(produtoSalvo, request.quantidade());

        return produtoSalvo;

    }

    public Produto buscarProdutoPorId(Long id){
        return produtoConsultaService.buscarProdutoPorId(id);
    }

    @Transactional
    public Produto atualizarProduto(Produto produto){

        validarProduto(produto);

        var produtoParaAtualizar = buscarProdutoPorId(produto.getId());
        produtoParaAtualizar.setNome(produto.getNome());
        produtoParaAtualizar.setDescricao(produto.getDescricao());
        produtoParaAtualizar.setPreco(produto.getPreco());
        produtoParaAtualizar.setAtualizadoEm(LocalDateTime.now());
        produtoParaAtualizar.setSku(produto.getSku());
        produtoParaAtualizar.setAtivo(produto.getAtivo());

        return produtoRepository.save(produtoParaAtualizar);

    }

    @Transactional
    public void deletarProduto(Long id){
        var produto = buscarProdutoPorId(id);
        produto.setAtivo(false);
        produto.setAtualizadoEm(LocalDateTime.now());
        produtoRepository.save(produto);
    }

    public void validarProduto(Produto produto){
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser nulo ou negativo");
        }
        if (produto.getSku() == null || produto.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU não pode ser vazio");
        }

    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade < 0){
            throw new IllegalArgumentException("A quantidade não pode ser nula ou menor que 0.");
        }

    }

}
