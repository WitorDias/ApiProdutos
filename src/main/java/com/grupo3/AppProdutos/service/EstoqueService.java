package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.auditoriaDTO.EstoqueAuditDTO;
import com.grupo3.AppProdutos.exception.EstoqueNaoEncontradoException;
import com.grupo3.AppProdutos.exception.QuantidadeEstoqueInvalidaException;
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
    private final AuditService auditService;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoConsultaService produtoConsultaService, AuditService auditService) {
        this.estoqueRepository = estoqueRepository;
        this.produtoConsultaService = produtoConsultaService;
        this.auditService = auditService;
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

        Estoque estoqueSalvo = estoqueRepository.save(estoque);
        auditService.registrar("Estoque", estoqueSalvo.getId(), TipoOperacao.CREATE, null, toEstoqueAuditDTO(estoqueSalvo));
        return estoqueSalvo;
    }

    public Estoque buscarEstoquePorProdutoId(Long produtoId){

        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        return estoqueRepository.findByProduto(produto).orElseThrow(
                () -> new EstoqueNaoEncontradoException(produtoId)
        );

    }

    public Estoque atualizarQuantidadeEstoque(Long produtoId, Integer novaQuantidade){

        validarQuantidade(novaQuantidade);
        Estoque estoque = buscarEstoquePorProdutoId(produtoId);

        // Captura estado anterior ANTES das alterações
        EstoqueAuditDTO estadoAnterior = toEstoqueAuditDTO(estoque);

        estoque.setQuantidade(novaQuantidade);
        estoque.setAtualizadoEm(LocalDateTime.now());

        Estoque estoqueAtualizado = estoqueRepository.save(estoque);
        auditService.registrar("Estoque", estoqueAtualizado.getId(), TipoOperacao.UPDATE, estadoAnterior, toEstoqueAuditDTO(estoqueAtualizado));
        return estoqueAtualizado;
    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade < 0){
            throw new QuantidadeEstoqueInvalidaException();
        }

    }

    private EstoqueAuditDTO toEstoqueAuditDTO(Estoque estoque) {
        return new EstoqueAuditDTO(
                estoque.getId(),
                estoque.getProduto().getId(),
                estoque.getProduto().getNome(),
                estoque.getQuantidade(),
                estoque.getCriadoEm(),
                estoque.getAtualizadoEm()
        );
    }

}
