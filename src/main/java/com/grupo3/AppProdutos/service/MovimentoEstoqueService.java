package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.TipoMovimento;
import com.grupo3.AppProdutos.repository.EstoqueMovimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentoEstoqueService {

    private final EstoqueMovimentoRepository estoqueMovimentoRepository;
    private final EstoqueService estoqueService;
    private final ProdutoConsultaService produtoConsultaService;

    public MovimentoEstoqueService(EstoqueMovimentoRepository estoqueMovimentoRepository, EstoqueService estoqueService, ProdutoConsultaService produtoConsultaService) {
        this.estoqueMovimentoRepository = estoqueMovimentoRepository;
        this.estoqueService = estoqueService;
        this.produtoConsultaService = produtoConsultaService;
    }

    @Transactional
    public MovimentoEstoque registrarEntrada(Long produtoId, Integer quantidade){
        validarQuantidade(quantidade);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        Estoque estoque = estoqueService.buscarEstoquePorProdutoId(produtoId);
        estoqueService.atualizarQuantidadeEstoque(produtoId, estoque.getQuantidade() + quantidade);
        MovimentoEstoque movimento = MovimentoEstoque.builder()
                .produto(produto)
                .quantidade(quantidade)
                .tipoMovimento(TipoMovimento.ENTRADA)
                .criadoEm(LocalDateTime.now())
                .build();
        return estoqueMovimentoRepository.save(movimento);

    }

    public MovimentoEstoque registrarSaida(Long produtoId, Integer quantidade){
        validarQuantidade(quantidade);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        Estoque estoque = estoqueService.buscarEstoquePorProdutoId(produtoId);
        if(estoque.getQuantidade() < quantidade){
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getId() + " " + produto.getNome());
        }
        estoqueService.atualizarQuantidadeEstoque(produtoId, estoque.getQuantidade() - quantidade);
        MovimentoEstoque movimento = MovimentoEstoque.builder()
                .produto(produto)
                .quantidade(quantidade)
                .tipoMovimento(TipoMovimento.SAIDA)
                .criadoEm(LocalDateTime.now())
                .build();
        return estoqueMovimentoRepository.save(movimento);
    }

    public List<MovimentoEstoque> listarMovimentosPorProdutoId(Long produtoId){
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        return estoqueMovimentoRepository.findByProduto(produto);
    }


    public void deletarMovimentoEstoquePorProdutoId(Long id){
        var produtoParaDeletar = produtoConsultaService.buscarProdutoPorId(id);
        estoqueMovimentoRepository.deleteByProduto(produtoParaDeletar);
    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade < 0){
            throw new IllegalArgumentException("A quantidade não pode ser nula ou menor que 0.");
        }

    }
}
