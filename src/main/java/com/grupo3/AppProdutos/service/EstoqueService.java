package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.EstoqueRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoConsultaService produtoConsultaService;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoConsultaService produtoConsultaService) {
        this.estoqueRepository = estoqueRepository;
        this.produtoConsultaService = produtoConsultaService;
    }

    @Transactional
    public Estoque criarEstoqueParaProduto(Produto produto, Integer quantidadeInicial){

        validarQuantidade(quantidadeInicial);

        Estoque estoque = Estoque.builder()
                .produto(produto)
                .quantidade(quantidadeInicial)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return estoqueRepository.save(estoque);
    }

    public Estoque buscarEstoquePorProdutoId(Long produtoId){

        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        return estoqueRepository.findByProduto(produto).orElseThrow(
                () -> new RuntimeException("Estoque não encontrado para este produto.")
        );

    }

    public Estoque atualizarQuantidadeEstoque(Long produtoId, Integer novaQuantidade){

        validarQuantidade(novaQuantidade);
        Estoque estoque = buscarEstoquePorProdutoId(produtoId);
        estoque.setQuantidade(novaQuantidade);
        estoque.setAtualizadoEm(LocalDateTime.now());

        return estoqueRepository.save(estoque);
    }

    public void deletarEstoquePorProdutoId(Long produtoId){

        var produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        estoqueRepository.deleteByProdutoId(produto.getId());
    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade < 0){
            throw new IllegalArgumentException("A quantidade não pode ser nula ou menor que 0.");
        }

    }

}
