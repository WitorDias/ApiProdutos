package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.exception.EstoqueInsuficienteException;
import com.grupo3.AppProdutos.exception.QuantidadeInvalidaException;
import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.enums.TipoMovimento;
import com.grupo3.AppProdutos.repository.EstoqueMovimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovimentoEstoqueServiceTest {

    private EstoqueMovimentoRepository movimentoRepository;
    private EstoqueService estoqueService;
    private ProdutoConsultaService produtoConsultaService;
    private AuditService auditService;
    private MovimentoEstoqueService movimentoService;

    @BeforeEach
    void setup() {
        movimentoRepository = mock(EstoqueMovimentoRepository.class);
        estoqueService = mock(EstoqueService.class);
        produtoConsultaService = mock(ProdutoConsultaService.class);
        auditService = mock(AuditService.class);
        movimentoService = new MovimentoEstoqueService(movimentoRepository, estoqueService, produtoConsultaService, auditService);
    }

    @Test
    @DisplayName("Deve registrar entrada com sucesso")
    void deveRegistrarEntradaComSucesso() {
        Produto produto = Produto.builder().id(1L).nome("Mouse").build();
        Estoque estoque = Estoque.builder().id(10L).produto(produto).quantidade(5).build();

        MovimentoEstoque salvo = MovimentoEstoque.builder()
                .id(100L)
                .produto(produto)
                .quantidade(10)
                .tipoMovimento(TipoMovimento.ENTRADA)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
        when(movimentoRepository.save(any())).thenReturn(salvo);

        MovimentoEstoque r = movimentoService.registrarEntrada(1L, 10);

        assertEquals(100L, r.getId());
        assertEquals(TipoMovimento.ENTRADA, r.getTipoMovimento());
        verify(estoqueService).atualizarQuantidadeEstoque(1L, 15);
        verify(auditService).registrar(eq("MovimentoEstoque"), eq(100L), eq(TipoOperacao.CREATE), isNull(), any());
    }

    @Test
    @DisplayName("Deve lançar QuantidadeInvalidaException ao registrar entrada inválida")
    void deveLancarQuantidadeInvalidaExceptionAoRegistrarEntrada() {
        assertThrows(QuantidadeInvalidaException.class,
                () -> movimentoService.registrarEntrada(1L, 0));
    }

    @Test
    @DisplayName("Deve registrar saída com sucesso")
    void deveRegistrarSaidaComSucesso() {
        Produto produto = Produto.builder().id(1L).nome("Monitor").build();
        Estoque estoque = Estoque.builder().id(55L).produto(produto).quantidade(20).build();

        MovimentoEstoque salvo = MovimentoEstoque.builder()
                .id(200L)
                .produto(produto)
                .quantidade(5)
                .tipoMovimento(TipoMovimento.SAIDA)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
        when(movimentoRepository.save(any())).thenReturn(salvo);

        MovimentoEstoque r = movimentoService.registrarSaida(1L, 5);

        assertEquals(200L, r.getId());
        assertEquals(TipoMovimento.SAIDA, r.getTipoMovimento());
        verify(estoqueService).atualizarQuantidadeEstoque(1L, 15);
        verify(auditService).registrar(eq("MovimentoEstoque"), eq(200L), eq(TipoOperacao.CREATE), isNull(), any());
    }

    @Test
    @DisplayName("Deve lançar QuantidadeInvalidaException ao registrar saída com quantidade inválida")
    void deveLancarQuantidadeInvalidaException() {
        assertThrows(QuantidadeInvalidaException.class,
                () -> movimentoService.registrarSaida(1L, -10));
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao tentar registrar saída maior que o estoque")
    void deveLancarEstoqueInsuficienteExceptionAoRegistrarSaida() {
        Produto produto = Produto.builder().id(1L).nome("Cadeira").build();
        Estoque estoque = Estoque.builder().id(99L).produto(produto).quantidade(3).build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);

        assertThrows(EstoqueInsuficienteException.class,
                () -> movimentoService.registrarSaida(1L, 5));
    }

    @Test
    @DisplayName("Deve listar movimentos de estoque por ID do produto")
    void deveListarMovimentosPorProdutoId() {
        Produto produto = Produto.builder().id(1L).nome("Webcam").build();

        List<MovimentoEstoque> lista = List.of(
                MovimentoEstoque.builder().id(1L).produto(produto).quantidade(10).build(),
                MovimentoEstoque.builder().id(2L).produto(produto).quantidade(5).build()
        );

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(movimentoRepository.findByProduto_Id(1L)).thenReturn(lista);

        List<MovimentoEstoque> r = movimentoService.listarMovimentosPorProdutoId(1L);

        assertEquals(2, r.size());
        assertEquals(1L, r.getFirst().getId());
        verify(movimentoRepository).findByProduto_Id(1L);
    }
}
