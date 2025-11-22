package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.exception.ProdutoNaoEncontradoException;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoConsultaServiceTest {

    private ProdutoRepository produtoRepository;
    private ProdutoConsultaService produtoConsultaService;

    @BeforeEach
    void setup() {
        produtoRepository = mock(ProdutoRepository.class);
        produtoConsultaService = new ProdutoConsultaService(produtoRepository);
    }

    @Test
    @DisplayName("Deve buscar produto ativo por ID com sucesso")
    void deveBuscarProdutoPorId() {
        Produto produto = Produto.builder().id(1L).nome("Teclado").ativo(true).build();
        when(produtoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(produto));

        Produto resultado = produtoConsultaService.buscarProdutoPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Teclado", resultado.getNome());
        verify(produtoRepository).findByIdAndAtivoTrue(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar produto ativo por ID")
    void deveLancarExcecaoProdutoNaoEncontrado() {
        when(produtoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> produtoConsultaService.buscarProdutoPorId(1L));
    }
}
