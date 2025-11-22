package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Carrinho;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.StatusCarrinho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CarrinhoRepositoryTest {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Carrinho cart1Aberto;
    private Carrinho cart1Finalizado;
    private Carrinho cart2Aberto;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        usuario1 = usuarioRepository.save(Usuario.builder()
                .nome("User 1")
                .email("user1@carrinho.com")
                .senha("123")
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        usuario2 = usuarioRepository.save(Usuario.builder()
                .nome("User 2")
                .email("user2@carrinho.com")
                .senha("456")
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        cart1Aberto = carrinhoRepository.save(Carrinho.builder()
                .usuario(usuario1)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .build());

        cart1Finalizado = carrinhoRepository.save(Carrinho.builder()
                .usuario(usuario1)
                .statusCarrinho(StatusCarrinho.FINALIZADO)
                .build());

        cart2Aberto = carrinhoRepository.save(Carrinho.builder()
                .usuario(usuario2)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .build());
    }

    @Test
    @DisplayName("Deve salvar Carrinho e aplicar timestamps e status corretamente")
    void deveSalvarCarrinhoEVerificarTimestamps() {
        Carrinho novoCarrinho = Carrinho.builder()
                .usuario(usuario2)
                .statusCarrinho(StatusCarrinho.CANCELADO)
                .build();

        Carrinho salvo = carrinhoRepository.save(novoCarrinho);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCriadoEm()).isNotNull();
        assertThat(salvo.getAtualizadoEm()).isNotNull();
        assertThat(salvo.getStatusCarrinho()).isEqualTo(StatusCarrinho.CANCELADO);
    }

    @Test
    @DisplayName("Deve encontrar Carrinho pelo Usuario e Status ABERTO")
    void deveEncontrarCarrinhoPorUsuarioEStatusAberto() {
        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario1, StatusCarrinho.ABERTO);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(cart1Aberto.getId());
        assertThat(encontrado.get().getUsuario().getEmail()).isEqualTo(usuario1.getEmail());
    }

    @Test
    @DisplayName("Deve encontrar Carrinho pelo Usuario e Status FINALIZADO")
    void deveEncontrarCarrinhoPorUsuarioEStatusFinalizado() {
        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario1, StatusCarrinho.FINALIZADO);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(cart1Finalizado.getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o Carrinho não existe para o Status buscado")
    void deveRetornarOptionalVazioQuandoCarrinhoNaoExisteParaStatus() {
        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario2, StatusCarrinho.FINALIZADO);
        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o Usuario não existe")
    void deveRetornarOptionalVazioQuandoUsuarioNaoExiste() {
        Usuario usuarioInexistente = Usuario.builder().id(999L).build();
        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuarioInexistente, StatusCarrinho.ABERTO);
        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar true se existe Carrinho ABERTO para o Usuario 1")
    void deveRetornarTrueSeExisteCarrinhoAbertoParaUsuario1() {
        boolean existe = carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario1, StatusCarrinho.ABERTO);
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar true se existe Carrinho FINALIZADO para o Usuario 1")
    void deveRetornarTrueSeExisteCarrinhoFinalizadoParaUsuario1() {
        boolean existe = carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario1, StatusCarrinho.FINALIZADO);
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se não existe Carrinho CANCELADO para o Usuario 2")
    void deveRetornarFalseSeNaoExisteCarrinhoCanceladoParaUsuario2() {
        boolean existe = carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario2, StatusCarrinho.CANCELADO);
        assertThat(existe).isFalse();
    }
}