package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.auditoriaDTO.EstoqueAuditDTO;
import com.grupo3.AppProdutos.exception.EstoqueNaoEncontradoException;
import com.grupo3.AppProdutos.exception.QuantidadeEstoqueInvalidaException;
import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstoqueServiceTest {

    private EstoqueRepository estoqueRepository;
    private ProdutoConsultaService produtoConsultaService;
    private AuditService auditService;
    private EstoqueService estoqueService;

    @BeforeEach
    void setup() {
        estoqueRepository = mock(EstoqueRepository.class);
        produtoConsultaService = mock(ProdutoConsultaService.class);
        auditService = mock(AuditService.class);
        estoqueService = new EstoqueService(estoqueRepository, produtoConsultaService, auditService);
    }

    @Test
    @DisplayName("Deve criar estoque para o produto")
    void deveCriarEstoqueParaProduto() {
        Produto produto = Produto.builder().id(1L).nome("Teclado").build();
        Estoque estoque = Estoque.builder()
                .id(10L)
                .produto(produto)
                .quantidade(20)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(estoqueRepository.save(any(Estoque.class))).thenReturn(estoque);

        Estoque resultado = estoqueService.criarEstoqueParaProduto(produto, 20);

        assertEquals(10L, resultado.getId());
        assertEquals(20, resultado.getQuantidade());
        verify(auditService).registrar(eq("Estoque"), eq(10L), eq(TipoOperacao.CREATE), isNull(), any(EstoqueAuditDTO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar estoque com quantidade inválida")
    void deveLancarExcecaoQuantidadeInvalidaAoCriar() {
        Produto produto = Produto.builder().id(1L).build();
        assertThrows(QuantidadeEstoqueInvalidaException.class,
                () -> estoqueService.criarEstoqueParaProduto(produto, -1));
    }

    @Test
    @DisplayName("Deve buscar estoque por ID do produto")
    void deveBuscarEstoquePorProdutoId() {
        Produto produto = Produto.builder().id(1L).build();
        Estoque estoque = Estoque.builder().id(5L).produto(produto).quantidade(10).build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueRepository.findByProduto(produto)).thenReturn(Optional.of(estoque));

        Estoque resultado = estoqueService.buscarEstoquePorProdutoId(1L);

        assertEquals(5L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque não for encontrado")
    void deveLancarExcecaoEstoqueNaoEncontrado() {
        Produto produto = Produto.builder().id(1L).build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueRepository.findByProduto(produto)).thenReturn(Optional.empty());

        assertThrows(EstoqueNaoEncontradoException.class,
                () -> estoqueService.buscarEstoquePorProdutoId(1L));
    }

    @Test
    @DisplayName("Deve atualizar a quantidade do estoque")
    void deveAtualizarQuantidadeEstoque() {
        Produto produto = Produto.builder().id(1L).nome("Teclado").build();
        Estoque estoque = Estoque.builder()
                .id(7L)
                .produto(produto)
                .quantidade(10)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        Estoque atualizado = Estoque.builder()
                .id(7L)
                .produto(produto)
                .quantidade(30)
                .criadoEm(estoque.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(estoqueRepository.findByProduto(produto)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(estoque)).thenReturn(atualizado);

        Estoque result = estoqueService.atualizarQuantidadeEstoque(1L, 30);

        assertEquals(30, result.getQuantidade());
        verify(auditService).registrar(eq("Estoque"), eq(7L), eq(TipoOperacao.UPDATE),
                any(EstoqueAuditDTO.class), any(EstoqueAuditDTO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quantidade inválida do estoque")
    void deveLancarExcecaoQuantidadeInvalidaAoAtualizar() {
        assertThrows(QuantidadeEstoqueInvalidaException.class,
                () -> estoqueService.atualizarQuantidadeEstoque(1L, -5));
    }
}
