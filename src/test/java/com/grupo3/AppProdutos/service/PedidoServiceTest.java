package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.PedidoDTO.ItemPedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoResponse;
import com.grupo3.AppProdutos.dto.auditoriaDTO.PedidoAuditDTO;
import com.grupo3.AppProdutos.exception.*;
import com.grupo3.AppProdutos.model.ItemPedido;
import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import com.grupo3.AppProdutos.repository.PedidoRepository;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PedidoService")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentoEstoqueService movimentoEstoqueService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@teste.com");
        usuario.setAtivo(true);
        return usuario;
    }

    private Produto criarProduto() {
        return Produto.builder()
                .id(1L)
                .nome("Produto Teste")
                .preco(new BigDecimal("100.00"))
                .build();
    }

    private Pedido criarPedido() {
        Usuario usuario = criarUsuario();
        Produto produto = criarProduto();

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.NOVO);
        pedido.setValorTotal(new BigDecimal("200.00"));
        pedido.setCriadoEm(LocalDateTime.now());

        ItemPedido item = ItemPedido.builder()
                .id(1L)
                .pedido(pedido)
                .produto(produto)
                .quantidade(2)
                .precoUnitario(new BigDecimal("100.00"))
                .precoTotal(new BigDecimal("200.00"))
                .build();

        pedido.setItens(new ArrayList<>(List.of(item)));
        return pedido;
    }

    @Nested
    @DisplayName("Criar Pedido")
    class CriarPedido {

        @Test
        @DisplayName("Deve criar pedido com sucesso")
        void deveCriarPedidoComSucesso() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();

            ItemPedidoRequest itemRequest = new ItemPedidoRequest(1L, 2);
            PedidoRequest request = new PedidoRequest(1L, List.of(itemRequest));

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
                Pedido p = invocation.getArgument(0);
                p.setId(1L);
                return p;
            });

            PedidoResponse resultado = pedidoService.criarPedido(request);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            assertNotNull(resultado.valorTotal());
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
            verify(auditService, times(1)).registrar(
                    eq("Pedido"),
                    eq(1L),
                    eq(TipoOperacao.CREATE),
                    isNull(),
                    any(PedidoAuditDTO.class)
            );
        }

        @Test
        @DisplayName("Deve criar pedido com múltiplos itens")
        void deveCriarPedidoComMultiplosItens() {
            Usuario usuario = criarUsuario();
            Produto produto1 = criarProduto();
            Produto produto2 = Produto.builder()
                    .id(2L)
                    .nome("Produto 2")
                    .preco(new BigDecimal("50.00"))
                    .build();

            ItemPedidoRequest item1 = new ItemPedidoRequest(1L, 2);
            ItemPedidoRequest item2 = new ItemPedidoRequest(2L, 1);
            PedidoRequest request = new PedidoRequest(1L, List.of(item1, item2));

            Pedido pedidoSalvo = criarPedido();

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto1));
            when(produtoRepository.findById(2L)).thenReturn(Optional.of(produto2));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

            PedidoResponse resultado = pedidoService.criarPedido(request);

            assertNotNull(resultado);
            verify(produtoRepository, times(1)).findById(1L);
            verify(produtoRepository, times(1)).findById(2L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            ItemPedidoRequest itemRequest = new ItemPedidoRequest(1L, 2);
            PedidoRequest request = new PedidoRequest(999L, List.of(itemRequest));

            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () -> pedidoService.criarPedido(request));

            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando produto não encontrado")
        void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
            Usuario usuario = criarUsuario();
            ItemPedidoRequest itemRequest = new ItemPedidoRequest(999L, 2);
            PedidoRequest request = new PedidoRequest(1L, List.of(itemRequest));

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ProdutoNaoEncontradoException.class, () -> pedidoService.criarPedido(request));

            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve calcular valor total corretamente")
        void deveCalcularValorTotalCorretamente() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();

            ItemPedidoRequest itemRequest = new ItemPedidoRequest(1L, 3);
            PedidoRequest request = new PedidoRequest(1L, List.of(itemRequest));

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
                Pedido p = invocation.getArgument(0);
                p.setId(1L);
                return p;
            });

            pedidoService.criarPedido(request);

            verify(pedidoRepository, times(1)).save(argThat(pedido ->
                    pedido.getValorTotal().compareTo(new BigDecimal("300.00")) == 0
            ));
        }
    }

    @Nested
    @DisplayName("Buscar Pedido por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar pedido por ID com sucesso")
        void deveBuscarPedidoPorIdComSucesso() {
            Pedido pedido = criarPedido();
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            PedidoResponse resultado = pedidoService.buscarPedidoPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            verify(pedidoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando pedido não encontrado")
        void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
            when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PedidoNaoEncontradoException.class, () -> pedidoService.buscarPedidoPorId(999L));
        }
    }

    @Nested
    @DisplayName("Buscar Pedidos por Usuário")
    class BuscarPorUsuario {

        @Test
        @DisplayName("Deve buscar pedidos do usuário com sucesso")
        void deveBuscarPedidosDoUsuarioComSucesso() {
            Usuario usuario = criarUsuario();
            Pedido pedido1 = criarPedido();
            Pedido pedido2 = criarPedido();
            pedido2.setId(2L);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(pedidoRepository.findByUsuarioId(1L)).thenReturn(List.of(pedido1, pedido2));

            List<PedidoResponse> resultado = pedidoService.buscarPedidoPorUsuario(1L);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(pedidoRepository, times(1)).findByUsuarioId(1L);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando usuário sem pedidos")
        void deveRetornarListaVaziaQuandoUsuarioSemPedidos() {
            Usuario usuario = criarUsuario();
            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(pedidoRepository.findByUsuarioId(1L)).thenReturn(List.of());

            List<PedidoResponse> resultado = pedidoService.buscarPedidoPorUsuario(1L);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                pedidoService.buscarPedidoPorUsuario(999L)
            );
        }
    }

    @Nested
    @DisplayName("Atualizar Status do Pedido")
    class AtualizarStatus {

        @Test
        @DisplayName("Deve atualizar status de NOVO para CONFIRMADO")
        void deveAtualizarStatusDeNovoParaConfirmado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.NOVO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.CONFIRMADO);

            assertNotNull(resultado);
            verify(movimentoEstoqueService, times(1)).registrarSaida(1L, 2);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
            verify(auditService, times(1)).registrar(
                    eq("Pedido"),
                    eq(1L),
                    eq(TipoOperacao.UPDATE),
                    any(PedidoAuditDTO.class),
                    any(PedidoAuditDTO.class)
            );
        }

        @Test
        @DisplayName("Deve atualizar status de NOVO para CANCELADO")
        void deveAtualizarStatusDeNovoParaCancelado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.NOVO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.CANCELADO);

            assertNotNull(resultado);
            verify(movimentoEstoqueService, never()).registrarSaida(any(), any());
            verify(movimentoEstoqueService, never()).registrarEntrada(any(), any());
        }

        @Test
        @DisplayName("Deve atualizar status de CONFIRMADO para ENVIADO")
        void deveAtualizarStatusDeConfirmadoParaEnviado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.CONFIRMADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.ENVIADO);

            assertNotNull(resultado);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve atualizar status de CONFIRMADO para CANCELADO e desfazer estoque")
        void deveAtualizarStatusDeConfirmadoParaCanceladoEDesfazerEstoque() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.CONFIRMADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.CANCELADO);

            assertNotNull(resultado);
            verify(movimentoEstoqueService, times(1)).registrarEntrada(1L, 2);
        }

        @Test
        @DisplayName("Deve atualizar status de ENVIADO para ENTREGUE")
        void deveAtualizarStatusDeEnviadoParaEntregue() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.ENVIADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.ENTREGUE);

            assertNotNull(resultado);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve atualizar status de ENTREGUE para FINALIZADO")
        void deveAtualizarStatusDeEntregueParaFinalizado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.ENTREGUE);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            PedidoResponse resultado = pedidoService.atualizarStatus(1L, StatusPedido.FINALIZADO);

            assertNotNull(resultado);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando status já está definido")
        void deveLancarExcecaoQuandoStatusJaDefinido() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.NOVO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(StatusJaDefinidoException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.NOVO)
            );

            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção para transição inválida NOVO para ENVIADO")
        void deveLancarExcecaoParaTransicaoInvalidaNovoParaEnviado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.NOVO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(TransicaoStatusInvalidaException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.ENVIADO)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção para transição inválida CONFIRMADO para ENTREGUE")
        void deveLancarExcecaoParaTransicaoInvalidaConfirmadoParaEntregue() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.CONFIRMADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(TransicaoStatusInvalidaException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.ENTREGUE)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção para transição inválida ENVIADO para FINALIZADO")
        void deveLancarExcecaoParaTransicaoInvalidaEnviadoParaFinalizado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.ENVIADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(TransicaoStatusInvalidaException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.FINALIZADO)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando tentar alterar pedido CANCELADO")
        void deveLancarExcecaoQuandoTentarAlterarPedidoCancelado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.CANCELADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(PedidoImutavelException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.NOVO)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando tentar alterar pedido FINALIZADO")
        void deveLancarExcecaoQuandoTentarAlterarPedidoFinalizado() {
            Pedido pedido = criarPedido();
            pedido.setStatus(StatusPedido.FINALIZADO);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThrows(PedidoImutavelException.class, () ->
                pedidoService.atualizarStatus(1L, StatusPedido.CANCELADO)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando pedido não encontrado")
        void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
            when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PedidoNaoEncontradoException.class, () ->
                pedidoService.atualizarStatus(999L, StatusPedido.CONFIRMADO)
            );
        }
    }

    @Nested
    @DisplayName("Salvar Pedido")
    class SalvarPedido {

        @Test
        @DisplayName("Deve salvar pedido diretamente")
        void deveSalvarPedidoDiretamente() {
            Pedido pedido = criarPedido();
            when(pedidoRepository.save(pedido)).thenReturn(pedido);

            Pedido resultado = pedidoService.salvarPedido(pedido);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(pedidoRepository, times(1)).save(pedido);
        }
    }
}


