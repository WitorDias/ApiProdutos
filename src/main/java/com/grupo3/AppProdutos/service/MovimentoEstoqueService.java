package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.auditoriaDTO.MovimentoEstoqueAuditDTO;
import com.grupo3.AppProdutos.exception.EstoqueInsuficienteException;
import com.grupo3.AppProdutos.exception.QuantidadeInvalidaException;
import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.enums.TipoMovimento;
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
    private final AuditService auditService;

    public MovimentoEstoqueService(EstoqueMovimentoRepository estoqueMovimentoRepository, EstoqueService estoqueService, ProdutoConsultaService produtoConsultaService, AuditService auditService) {
        this.estoqueMovimentoRepository = estoqueMovimentoRepository;
        this.estoqueService = estoqueService;
        this.produtoConsultaService = produtoConsultaService;
        this.auditService = auditService;
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

        MovimentoEstoque salvo = estoqueMovimentoRepository.save(movimento);
        auditService.registrar("MovimentoEstoque", salvo.getId(), TipoOperacao.CREATE, null, toMovimentoAuditDTO(salvo));
        return salvo;

    }
    @Transactional
    public MovimentoEstoque registrarSaida(Long produtoId, Integer quantidade){
        validarQuantidade(quantidade);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        Estoque estoque = estoqueService.buscarEstoquePorProdutoId(produtoId);
        if(estoque.getQuantidade() < quantidade){
            throw new EstoqueInsuficienteException(estoque.getQuantidade(), quantidade);
        }
        estoqueService.atualizarQuantidadeEstoque(produtoId, estoque.getQuantidade() - quantidade);
        MovimentoEstoque movimento = MovimentoEstoque.builder()
                .produto(produto)
                .quantidade(quantidade)
                .tipoMovimento(TipoMovimento.SAIDA)
                .criadoEm(LocalDateTime.now())
                .build();

        MovimentoEstoque salvo = estoqueMovimentoRepository.save(movimento);
        auditService.registrar("MovimentoEstoque", salvo.getId(), TipoOperacao.CREATE, null, toMovimentoAuditDTO(salvo));
        return salvo;
    }

    public List<MovimentoEstoque> listarMovimentosPorProdutoId(Long produtoId){
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);
        return estoqueMovimentoRepository.findByProduto_Id(produto.getId());
    }

    private void validarQuantidade(Integer quantidade){

        if(quantidade == null || quantidade <= 0){
            throw new QuantidadeInvalidaException("A quantidade deve ser maior que 0.");
        }

    }

    private MovimentoEstoqueAuditDTO toMovimentoAuditDTO(MovimentoEstoque movimento) {
        return new MovimentoEstoqueAuditDTO(
                movimento.getId(),
                movimento.getProduto().getId(),
                movimento.getProduto().getNome(),
                movimento.getQuantidade(),
                movimento.getTipoMovimento(),
                movimento.getCriadoEm()
        );
    }
}
