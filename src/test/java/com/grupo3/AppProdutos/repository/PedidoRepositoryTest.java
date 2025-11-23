package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Pedido pedidoUser1Novo;
    private Pedido pedidoUser1Confirmado;
    private Pedido pedidoUser2Entregue;
    private Pedido pedidoUser2Finalizado;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        Usuario u1 = Usuario.builder()
                .nome("User Um")
                .email("user1@test.com")
                .senha("senha1")
                .ativo(true)
                .criadoEm(now.minusDays(5))
                .atualizadoEm(now.minusDays(5))
                .build();
        usuario1 = usuarioRepository.save(u1);

        Usuario u2 = Usuario.builder()
                .nome("User Dois")
                .email("user2@test.com")
                .senha("senha2")
                .ativo(true)
                .criadoEm(now.minusDays(5))
                .atualizadoEm(now.minusDays(5))
                .build();
        usuario2 = usuarioRepository.save(u2);

        Pedido p1 = Pedido.builder()
                .usuario(usuario1)
                .valorTotal(new BigDecimal("100.00"))
                .build();
        pedidoUser1Novo = pedidoRepository.save(p1);

        Pedido p2 = Pedido.builder()
                .usuario(usuario1)
                .valorTotal(new BigDecimal("200.00"))
                .build();
        pedidoUser1Confirmado = pedidoRepository.save(p2);
        pedidoUser1Confirmado.setStatus(StatusPedido.CONFIRMADO);
        pedidoUser1Confirmado = pedidoRepository.save(pedidoUser1Confirmado);

        Pedido p3 = Pedido.builder()
                .usuario(usuario2)
                .valorTotal(new BigDecimal("50.00"))
                .build();
        pedidoUser2Entregue = pedidoRepository.save(p3);
        pedidoUser2Entregue.setStatus(StatusPedido.ENTREGUE);
        pedidoUser2Entregue = pedidoRepository.save(pedidoUser2Entregue);

        Pedido p4 = Pedido.builder()
                .usuario(usuario2)
                .valorTotal(new BigDecimal("40.00"))
                .build();
        pedidoUser2Finalizado = pedidoRepository.save(p4);
        pedidoUser2Finalizado.setStatus(StatusPedido.FINALIZADO);
        pedidoUser2Finalizado = pedidoRepository.save(pedidoUser2Finalizado);
    }


    @Test
    @DisplayName("Deve encontrar todos os pedidos de um usuário específico")
    void deveEncontrarTodosOsPedidosDeUmUsuarioEspecifico() {
        List<Pedido> pedidosUser1 = pedidoRepository.findByUsuarioId(usuario1.getId());

        assertThat(pedidosUser1).hasSize(2);
        assertThat(pedidosUser1).extracting(p -> p.getUsuario().getId()).containsOnly(usuario1.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para usuário sem pedidos")
    void deveRetornarListaVaziaParaUsuarioSemPedidos() {
        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(999L);
        assertThat(pedidos).isEmpty();
    }


    @Test
    @DisplayName("Deve encontrar pedidos com status NOVO")
    void deveEncontrarPedidosComStatusNovo() {
        List<Pedido> pedidosNovos = pedidoRepository.findByStatus(StatusPedido.NOVO);

        assertThat(pedidosNovos).hasSize(1);
        assertThat(pedidosNovos.getFirst().getId()).isEqualTo(pedidoUser1Novo.getId());
        assertThat(pedidosNovos.getFirst().getStatus()).isEqualTo(StatusPedido.NOVO);
    }

    @Test
    @DisplayName("Deve encontrar pedidos com status CONFIRMADO")
    void deveEncontrarPedidosComStatusConfirmado() {
        List<Pedido> pedidosConfirmados = pedidoRepository.findByStatus(StatusPedido.CONFIRMADO);

        assertThat(pedidosConfirmados).hasSize(1);
        assertThat(pedidosConfirmados.getFirst().getId()).isEqualTo(pedidoUser1Confirmado.getId());
        assertThat(pedidosConfirmados.getFirst().getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
    }

    @Test
    @DisplayName("Deve encontrar pedidos com status ENTREGUE")
    void deveEncontrarPedidosComStatusEntregue() {
        List<Pedido> pedidosEntregues = pedidoRepository.findByStatus(StatusPedido.ENTREGUE);

        assertThat(pedidosEntregues).hasSize(1);
        assertThat(pedidosEntregues.getFirst().getId()).isEqualTo(pedidoUser2Entregue.getId());
        assertThat(pedidosEntregues.getFirst().getStatus()).isEqualTo(StatusPedido.ENTREGUE);
    }

    @Test
    @DisplayName("Deve encontrar pedidos com status FINALIZADO")
    void deveEncontrarPedidosComStatusFinalizado() {
        List<Pedido> pedidosFinalizados = pedidoRepository.findByStatus(StatusPedido.FINALIZADO);

        assertThat(pedidosFinalizados).hasSize(1);
        assertThat(pedidosFinalizados.getFirst().getId()).isEqualTo(pedidoUser2Finalizado.getId());
        assertThat(pedidosFinalizados.getFirst().getStatus()).isEqualTo(StatusPedido.FINALIZADO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia para status CANCELADO quando nenhum pedido for cancelado")
    void deveRetornarListaVaziaParaStatusCancelado() {
        List<Pedido> pedidos = pedidoRepository.findByStatus(StatusPedido.CANCELADO);
        assertThat(pedidos).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar o pedido e aplicar o PrePersist corretamente")
    void deveSalvarPedidoEVerificarPrePersist() {
        Pedido novoPedido = Pedido.builder()
                .usuario(usuario2)
                .valorTotal(new BigDecimal("350.50"))
                .build();

        Pedido salvo = pedidoRepository.save(novoPedido);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCriadoEm()).isNotNull();
        assertThat(salvo.getStatus()).isEqualTo(StatusPedido.NOVO);
        assertThat(salvo.getValorTotal()).isEqualByComparingTo("350.50");
    }
}