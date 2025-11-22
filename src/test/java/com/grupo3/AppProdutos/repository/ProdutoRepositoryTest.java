package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoRepositoryTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve retornar produto ativo ao buscar por ID")
    void deveRetornarProdutoAtivoPorId() {
        Categoria categoria = Categoria.builder().id(1L).nome("Eletrônicos").build();

        Produto produto = Produto.builder()
                .id(1L)
                .nome("Teclado")
                .sku("SKU1")
                .preco(BigDecimal.TEN)
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(produto));

        Optional<Produto> result = produtoRepository.findByIdAndAtivoTrue(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(produtoRepository).findByIdAndAtivoTrue(1L);
    }

    @Test
    @DisplayName("Deve retornar lista de produtos ativos")
    void deveRetornarListaProdutosAtivos() {
        Categoria categoria = Categoria.builder().id(1L).nome("Eletrônicos").build();

        Produto produto = Produto.builder()
                .id(2L)
                .nome("Mouse")
                .sku("SKU2")
                .preco(BigDecimal.ONE)
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findAllByAtivoTrue()).thenReturn(List.of(produto));

        List<Produto> lista = produtoRepository.findAllByAtivoTrue();

        assertEquals(1, lista.size());
        assertEquals("Mouse", lista.getFirst().getNome());
        verify(produtoRepository).findAllByAtivoTrue();
    }

    @Test
    @DisplayName("Deve retornar produto ao buscar por SKU")
    void deveRetornarProdutoPorSku() {
        Categoria categoria = Categoria.builder().id(1L).nome("Eletrônicos").build();

        Produto produto = Produto.builder()
                .id(3L)
                .nome("Monitor")
                .sku("SKU_MONITOR")
                .preco(BigDecimal.valueOf(500))
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findBySku("SKU_MONITOR")).thenReturn(Optional.of(produto));

        Optional<Produto> result = produtoRepository.findBySku("SKU_MONITOR");

        assertTrue(result.isPresent());
        assertEquals("Monitor", result.get().getNome());
        verify(produtoRepository).findBySku("SKU_MONITOR");
    }

    @Test
    @DisplayName("Deve retornar Optional.empty quando SKU não existe")
    void deveRetornarEmptyQuandoSkuNaoExiste() {
        when(produtoRepository.findBySku("INEXISTENTE")).thenReturn(Optional.empty());

        Optional<Produto> result = produtoRepository.findBySku("INEXISTENTE");

        assertTrue(result.isEmpty());
        verify(produtoRepository).findBySku("INEXISTENTE");
    }
}
