package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.CarrinhoResponse;
import com.grupo3.AppProdutos.exception.*;
import com.grupo3.AppProdutos.mapper.CarrinhoMapper;
import com.grupo3.AppProdutos.model.*;
import com.grupo3.AppProdutos.model.enums.StatusCarrinho;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import com.grupo3.AppProdutos.repository.CarrinhoRepository;
import com.grupo3.AppProdutos.repository.ItemCarrinhoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CarrinhoService")
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoConsultaService produtoConsultaService;

    @Mock
    private CarrinhoMapper carrinhoMapper;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CarrinhoService carrinhoService;

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

    private Estoque criarEstoque(Produto produto, Integer quantidade) {
        return Estoque.builder()
                .id(1L)
                .produto(produto)
                .quantidade(quantidade)
                .build();
    }

    private Carrinho criarCarrinho(Usuario usuario) {
        Carrinho carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setUsuario(usuario);
        carrinho.setStatusCarrinho(StatusCarrinho.ABERTO);
        carrinho.setCriadoEm(LocalDateTime.now());
        carrinho.setAtualizadoEm(LocalDateTime.now());
        carrinho.setItens(new ArrayList<>());
        return carrinho;
    }

    @Nested
    @DisplayName("Buscar Carrinho Ativo")
    class BuscarCarrinhoAtivo {

        @Test
        @DisplayName("Deve buscar carrinho ativo existente")
        void deveBuscarCarrinhoAtivoExistente() {
            Usuario usuario = criarUsuario();
            Carrinho carrinho = criarCarrinho(usuario);
            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(carrinhoMapper.toResponse(carrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.buscarCarrinhoAtivo(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            verify(carrinhoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar novo carrinho quando não existe ativo")
        void deveCriarNovoCarrinhoQuandoNaoExiste() {
            Usuario usuario = criarUsuario();
            Carrinho novoCarrinho = criarCarrinho(usuario);
            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.empty());
            when(carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(false);
            when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(novoCarrinho);
            when(carrinhoMapper.toResponse(novoCarrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.buscarCarrinhoAtivo(1L);

            assertNotNull(resultado);
            verify(carrinhoRepository, times(1)).save(any(Carrinho.class));
            verify(auditService, times(1)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () -> {
                carrinhoService.buscarCarrinhoAtivo(999L);
            });
        }
    }

    @Nested
    @DisplayName("Adicionar Item ao Carrinho")
    class AdicionarItem {

        @Test
        @DisplayName("Deve adicionar novo item ao carrinho com sucesso")
        void deveAdicionarNovoItemComSucesso() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Estoque estoque = criarEstoque(produto, 100);
            Carrinho carrinho = criarCarrinho(usuario);

            AdicionarItemCarrinhoRequest request = new AdicionarItemCarrinhoRequest(1L, 10);
            ItemCarrinho novoItem = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(10)
                    .capturaPreco(produto.getPreco())
                    .build();

            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.empty());
            when(itemCarrinhoRepository.save(any(ItemCarrinho.class))).thenReturn(novoItem);
            when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
            when(carrinhoMapper.toResponse(carrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.adicionarItem(1L, request);

            assertNotNull(resultado);
            verify(itemCarrinhoRepository, times(1)).save(any(ItemCarrinho.class));
            verify(auditService, times(2)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve atualizar quantidade quando item já existe")
        void deveAtualizarQuantidadeQuandoItemJaExiste() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Estoque estoque = criarEstoque(produto, 100);
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho itemExistente = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(5)
                    .capturaPreco(produto.getPreco())
                    .build();

            AdicionarItemCarrinhoRequest request = new AdicionarItemCarrinhoRequest(1L, 10);
            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.of(itemExistente));
            when(itemCarrinhoRepository.save(itemExistente)).thenReturn(itemExistente);
            when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(carrinho));
            when(carrinhoMapper.toResponse(carrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.adicionarItem(1L, request);

            assertNotNull(resultado);
            assertEquals(15, itemExistente.getQuantidade());
            verify(itemCarrinhoRepository, times(1)).save(itemExistente);
        }

        @Test
        @DisplayName("Deve lançar exceção quando estoque insuficiente")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Estoque estoque = criarEstoque(produto, 5);
            Carrinho carrinho = criarCarrinho(usuario);

            AdicionarItemCarrinhoRequest request = new AdicionarItemCarrinhoRequest(1L, 10);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.empty());

            assertThrows(EstoqueInsuficienteException.class, () -> {
                carrinhoService.adicionarItem(1L, request);
            });

            verify(itemCarrinhoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando quantidade inválida")
        void deveLancarExcecaoQuandoQuantidadeInvalida() {
            AdicionarItemCarrinhoRequest request = new AdicionarItemCarrinhoRequest(1L, 0);

            assertThrows(QuantidadeInvalidaException.class, () -> {
                carrinhoService.adicionarItem(1L, request);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando quantidade é negativa")
        void deveLancarExcecaoQuandoQuantidadeNegativa() {
            AdicionarItemCarrinhoRequest request = new AdicionarItemCarrinhoRequest(1L, -5);

            assertThrows(QuantidadeInvalidaException.class, () -> {
                carrinhoService.adicionarItem(1L, request);
            });
        }
    }

    @Nested
    @DisplayName("Atualizar Item do Carrinho")
    class AtualizarItem {

        @Test
        @DisplayName("Deve atualizar quantidade do item com sucesso")
        void deveAtualizarQuantidadeComSucesso() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Estoque estoque = criarEstoque(produto, 100);
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho item = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(5)
                    .capturaPreco(produto.getPreco())
                    .build();

            AtualizarItemCarrinhoRequest request = new AtualizarItemCarrinhoRequest(15);
            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.of(item));
            when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);
            when(itemCarrinhoRepository.save(item)).thenReturn(item);
            when(carrinhoMapper.toResponse(carrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.atualizarItem(1L, 1L, request);

            assertNotNull(resultado);
            assertEquals(15, item.getQuantidade());
            verify(auditService, times(1)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando item não está no carrinho")
        void deveLancarExcecaoQuandoItemNaoEstaNoCarrinho() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Carrinho carrinho = criarCarrinho(usuario);
            AtualizarItemCarrinhoRequest request = new AtualizarItemCarrinhoRequest(10);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.empty());

            assertThrows(ItemNaoEstaNoCarrinhoException.class, () -> {
                carrinhoService.atualizarItem(1L, 1L, request);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando estoque insuficiente")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Estoque estoque = criarEstoque(produto, 5);
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho item = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(5)
                    .capturaPreco(produto.getPreco())
                    .build();

            AtualizarItemCarrinhoRequest request = new AtualizarItemCarrinhoRequest(20);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.of(item));
            when(estoqueService.buscarEstoquePorProdutoId(1L)).thenReturn(estoque);

            assertThrows(EstoqueInsuficienteException.class, () -> {
                carrinhoService.atualizarItem(1L, 1L, request);
            });
        }
    }

    @Nested
    @DisplayName("Remover Item do Carrinho")
    class RemoverItem {

        @Test
        @DisplayName("Deve remover item do carrinho com sucesso")
        void deveRemoverItemComSucesso() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho item = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(5)
                    .capturaPreco(produto.getPreco())
                    .build();

            carrinho.getItens().add(item);
            CarrinhoResponse response = new CarrinhoResponse(1L, 1L, StatusCarrinho.ABERTO, new ArrayList<>(), BigDecimal.ZERO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto))
                    .thenReturn(Optional.of(item));
            when(carrinhoMapper.toResponse(carrinho)).thenReturn(response);

            CarrinhoResponse resultado = carrinhoService.removerItem(1L, 1L);

            assertNotNull(resultado);
            verify(itemCarrinhoRepository, times(1)).delete(item);
            verify(auditService, times(2)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando carrinho não encontrado")
        void deveLancarExcecaoQuandoCarrinhoNaoEncontrado() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(produtoConsultaService.buscarProdutoPorId(1L)).thenReturn(produto);
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.empty());

            assertThrows(CarrinhoNaoEncontradoException.class, () -> {
                carrinhoService.removerItem(1L, 1L);
            });
        }
    }

    @Nested
    @DisplayName("Limpar Carrinho")
    class LimparCarrinho {

        @Test
        @DisplayName("Deve limpar todos os itens do carrinho")
        void deveLimparTodosItens() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho item1 = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(5)
                    .capturaPreco(new BigDecimal("100.00"))
                    .build();
            ItemCarrinho item2 = ItemCarrinho.builder()
                    .id(2L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(3)
                    .capturaPreco(new BigDecimal("100.00"))
                    .build();

            carrinho.getItens().add(item1);
            carrinho.getItens().add(item2);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));

            carrinhoService.limparCarrinho(1L);

            verify(itemCarrinhoRepository, times(1)).deleteAll(carrinho.getItens());
            verify(auditService, times(2)).registrar(any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Finalizar Carrinho")
    class FinalizarCarrinho {

        @Test
        @DisplayName("Deve finalizar carrinho e criar pedido com sucesso")
        void deveFinalizarCarrinhoComSucesso() {
            Usuario usuario = criarUsuario();
            Produto produto = criarProduto();
            Carrinho carrinho = criarCarrinho(usuario);

            ItemCarrinho item = ItemCarrinho.builder()
                    .id(1L)
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(2)
                    .capturaPreco(new BigDecimal("100.00"))
                    .build();

            carrinho.getItens().add(item);

            Pedido pedido = new Pedido();
            pedido.setId(1L);
            pedido.setUsuario(usuario);
            pedido.setStatus(StatusPedido.NOVO);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(pedidoService.salvarPedido(any(Pedido.class))).thenReturn(pedido);
            when(carrinhoRepository.save(carrinho)).thenReturn(carrinho);

            Long pedidoId = carrinhoService.finalizarCarrinho(1L);

            assertNotNull(pedidoId);
            assertEquals(1L, pedidoId);
            assertEquals(StatusCarrinho.FINALIZADO, carrinho.getStatusCarrinho());
            verify(pedidoService, times(1)).salvarPedido(any(Pedido.class));
            verify(auditService, times(2)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando carrinho vazio")
        void deveLancarExcecaoQuandoCarrinhoVazio() {
            Usuario usuario = criarUsuario();
            Carrinho carrinho = criarCarrinho(usuario);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));

            assertThrows(CarrinhoVazioException.class, () -> {
                carrinhoService.finalizarCarrinho(1L);
            });
        }
    }

    @Nested
    @DisplayName("Cancelar Carrinho")
    class CancelarCarrinho {

        @Test
        @DisplayName("Deve cancelar carrinho com sucesso")
        void deveCancelarCarrinhoComSucesso() {
            Usuario usuario = criarUsuario();
            Carrinho carrinho = criarCarrinho(usuario);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.of(carrinho));
            when(carrinhoRepository.save(carrinho)).thenReturn(carrinho);

            carrinhoService.cancelarCarrinho(1L);

            assertEquals(StatusCarrinho.CANCELADO, carrinho.getStatusCarrinho());
            verify(carrinhoRepository, times(1)).save(carrinho);
            verify(auditService, times(1)).registrar(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando carrinho não encontrado")
        void deveLancarExcecaoQuandoCarrinhoNaoEncontrado() {
            Usuario usuario = criarUsuario();

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO))
                    .thenReturn(Optional.empty());

            assertThrows(CarrinhoNaoEncontradoException.class, () -> {
                carrinhoService.cancelarCarrinho(1L);
            });
        }
    }
}

