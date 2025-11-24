package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.ProdutoDTO.CriarProdutoRequest;
import com.grupo3.AppProdutos.dto.ProdutoDTO.ProdutoRequest;
import com.grupo3.AppProdutos.dto.ProdutoDTO.ProdutoResponse;
import com.grupo3.AppProdutos.exception.CategoriaNaoEncontradaException;
import com.grupo3.AppProdutos.exception.SkuJaExisteException;
import com.grupo3.AppProdutos.exception.ValidacaoProdutoException;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private ProdutoConsultaService produtoConsultaService;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve buscar lista de produtos ativos")
    void deveBuscarListaDeProdutos() {

        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Roupas")
                .build();

        Produto produto = Produto.builder()
                .id(1L).nome("Mouse").ativo(true)
                .preco(BigDecimal.TEN)
                .categoria(categoria)
                .sku("ABC123")
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findAllByAtivoTrue()).thenReturn(List.of(produto));

        List<ProdutoResponse> lista = produtoService.buscarListaDeProdutos();

        assertEquals(1, lista.size());
        assertEquals("Mouse", lista.getFirst().nome());
        verify(produtoRepository).findAllByAtivoTrue();
    }

    @Test
    @DisplayName("Deve salvar produto com sucesso")
    void deveSalvarProdutoComSucesso() {

        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Mouse", "Mouse Gamer", BigDecimal.TEN, "SKU123", 1L, true
        );
        CriarProdutoRequest request = new CriarProdutoRequest(produtoRequest, 5);

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produtoCriado = Produto.builder()
                .id(1L).nome("Mouse")
                .preco(BigDecimal.TEN)
                .sku("SKU123")
                .categoria(categoria)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findBySku("SKU123")).thenReturn(Optional.empty());
        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(categoria);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoCriado);

        ProdutoResponse response = produtoService.salvarProduto(request);

        assertEquals("Mouse", response.nome());

        verify(produtoRepository).findBySku("SKU123");
        verify(estoqueService).criarEstoqueParaProduto(produtoCriado, 5);
        verify(auditService).registrar(eq("Produto"), eq(1L), eq(TipoOperacao.CREATE), isNull(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU já existe")
    void deveLancarSkuJaExisteException() {

        ProdutoRequest req = new ProdutoRequest(
                "Teste", "Desc", BigDecimal.ONE, "SKU_DUP", 2L, true
        );
        CriarProdutoRequest request = new CriarProdutoRequest(req, 1);

        when(produtoRepository.findBySku("SKU_DUP"))
                .thenReturn(Optional.of(new Produto()));

        assertThrows(SkuJaExisteException.class, () -> produtoService.salvarProduto(request));

        verify(produtoRepository).findBySku("SKU_DUP");
        verifyNoInteractions(estoqueService);
    }

    @Test
    @DisplayName("Deve lançar erro quando quantidade inicial é inválida")
    void deveLancarValidacaoProdutoException() {

        ProdutoRequest req = new ProdutoRequest("A", "B", BigDecimal.TEN, "SKU", 1L, true);

        CriarProdutoRequest request = new CriarProdutoRequest(req, 0);

        assertThrows(ValidacaoProdutoException.class,
                () -> produtoService.salvarProduto(request));
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        Categoria categoria = Categoria.builder()
                .id(1L)
                .build();
        Produto produto = Produto.builder()
                .id(10L).nome("Teclado")
                .preco(BigDecimal.ONE)
                .sku("SKUX")
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .ativo(true)
                .build();

        when(produtoConsultaService.buscarProdutoPorId(10L))
                .thenReturn(produto);

        ProdutoResponse response = produtoService.buscarProdutoPorId(10L);

        assertEquals("Teclado", response.nome());
        verify(produtoConsultaService).buscarProdutoPorId(10L);
    }

    @Test
    @DisplayName("Deve lançar CategoriaNaoEncontradaException ao salvar produto com categoria inválida")
    void deveLancarCategoriaNaoEncontradaException() {

        ProdutoRequest produtoRequest = new ProdutoRequest(
                "Teclado",
                "Teclado mecânico",
                BigDecimal.ONE,
                "SKUX",
                0L,
                true
        );

        CriarProdutoRequest criarRequest = new CriarProdutoRequest(produtoRequest, 20);

        when(categoriaService.buscarCategoriaPorId(0L))
                .thenThrow(new CategoriaNaoEncontradaException(0L));

        assertThrows(CategoriaNaoEncontradaException.class,
                () -> produtoService.salvarProduto(criarRequest));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        Produto produto = Produto.builder()
                .id(1L).nome("Antigo")
                .descricao("desc")
                .sku("OLD")
                .preco(BigDecimal.ONE)
                .categoria(new Categoria())
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        ProdutoRequest request = new ProdutoRequest(
                "Novo", "desc nova", BigDecimal.valueOf(20), "NEW", 1L, true
        );

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(categoria);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = produtoService.atualizarProduto(1L, request);

        assertEquals("Novo", response.nome());
        assertEquals("NEW", response.sku());

        verify(auditService).registrar(eq("Produto"), eq(1L), eq(TipoOperacao.UPDATE), any(), any());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar produto com preço inválido")
    void deveLancarValidacaoProdutoExceptionQuandoAtualizarComPrecoInvalido() {

        ProdutoRequest request = new ProdutoRequest(
                "X", "Y", BigDecimal.valueOf(-1), "SKU", 1L, true
        );

        assertThrows(ValidacaoProdutoException.class,
                () -> produtoService.atualizarProduto(1L, request));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProduto() {
        Categoria categoria = Categoria.builder().build();
        Produto produto = Produto.builder()
                .id(5L).nome("Produto")
                .ativo(true)
                .categoria(categoria)
                .preco(BigDecimal.ONE)
                .sku("AAA")
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoConsultaService.buscarProdutoPorId(5L)).thenReturn(produto);

        produtoService.deletarProduto(5L);

        assertFalse(produto.getAtivo());
        verify(produtoRepository).save(produto);
        verify(auditService).registrar(eq("Produto"), eq(5L),
                eq(TipoOperacao.DELETE), any(), isNull());
    }

    @Test
    @DisplayName("Deve buscar produtos ativos por parte do nome")
    void deveBuscarProdutosPorNome() {

        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Acessórios")
                .build();

        Produto p1 = Produto.builder()
                .id(1L)
                .nome("Mouse Gamer")
                .preco(BigDecimal.TEN)
                .sku("MOUSE01")
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        Produto p2 = Produto.builder()
                .id(2L)
                .nome("Mousepad Extra")
                .preco(BigDecimal.ONE)
                .sku("MP001")
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("mouse"))
                .thenReturn(List.of(p1, p2));

        List<ProdutoResponse> respostas = produtoService.buscarPorNome("mouse");

        assertEquals(2, respostas.size());
        assertTrue(respostas.stream().anyMatch(r -> r.nome().equals("Mouse Gamer")));
        assertTrue(respostas.stream().anyMatch(r -> r.nome().equals("Mousepad Extra")));

        verify(produtoRepository).findByNomeContainingIgnoreCaseAndAtivoTrue("mouse");
    }

    @Test
    @DisplayName("Deve buscar produtos ativos por nome da categoria")
    void deveBuscarProdutosPorNomeCategoria() {

        Categoria categoria = Categoria.builder()
                .id(10L)
                .nome("Eletrônicos")
                .build();

        Produto p1 = Produto.builder()
                .id(3L)
                .nome("Smartphone X")
                .preco(BigDecimal.ONE)
                .sku("SPX01")
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        Produto p2 = Produto.builder()
                .id(4L)
                .nome("Fone Bluetooth")
                .preco(BigDecimal.TEN)
                .sku("FBT01")
                .ativo(true)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .build();

        when(produtoRepository.findByCategoriaNomeIgnoreCaseAndAtivoTrue("eletrônicos"))
                .thenReturn(List.of(p1, p2));

        List<ProdutoResponse> respostas = produtoService.buscarPorNomeCategoria("eletrônicos");

        assertEquals(2, respostas.size());
        assertTrue(respostas.stream().anyMatch(r -> r.nome().equals("Smartphone X")));
        assertTrue(respostas.stream().anyMatch(r -> r.nome().equals("Fone Bluetooth")));

        verify(produtoRepository).findByCategoriaNomeIgnoreCaseAndAtivoTrue("eletrônicos");
    }


}
