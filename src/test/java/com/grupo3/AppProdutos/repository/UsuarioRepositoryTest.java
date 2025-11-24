package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Usuario;
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
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioAtivo;
    private Usuario usuarioInativo;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        usuarioAtivo = Usuario.builder()
                .nome("Alice Silva")
                .email("alice@teste.com")
                .senha("senha123")
                .ativo(true)
                .criadoEm(now.minusDays(1))
                .atualizadoEm(now.minusDays(1))
                .build();

        usuarioInativo = Usuario.builder()
                .nome("Bob Santos")
                .email("bob@teste.com")
                .senha("senha456")
                .ativo(false)
                .criadoEm(now.minusDays(2))
                .atualizadoEm(now.minusDays(2))
                .build();
    }

    @Test
    @DisplayName("Deve encontrar usuário ativo pelo ID e retornar Optional com valor")
    void deveEncontrarUsuarioAtivoPorIdERetornarOptionalComValor() {
        Usuario salvo = usuarioRepository.save(usuarioAtivo);
        Optional<Usuario> encontrado = usuarioRepository.findByIdAndAtivoTrue(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo(usuarioAtivo.getEmail());
        assertThat(encontrado.get().getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Não deve encontrar usuário inativo pelo ID, retornando Optional vazio")
    void naoDeveEncontrarUsuarioInativoPorIdRetornandoOptionalVazio() {
        Usuario salvoAtivo = usuarioRepository.save(usuarioInativo);

        salvoAtivo.setAtivo(false);
        Usuario salvoInativo = usuarioRepository.save(salvoAtivo);

        Optional<Usuario> encontrado = usuarioRepository.findByIdAndAtivoTrue(salvoInativo.getId());

        assertThat(encontrado).isNotPresent();
    }


    @Test
    @DisplayName("Deve retornar true se um usuário com o email especificado existir")
    void deveRetornarVerdadeiroSeEmailExistir() {
        usuarioRepository.save(usuarioAtivo);
        boolean existe = usuarioRepository.existsByEmail("alice@teste.com");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se o email especificado não existir")
    void deveRetornarFalsoSeEmailNaoExistir() {
        boolean existe = usuarioRepository.existsByEmail("naoexiste@teste.com");
        assertThat(existe).isFalse();
    }


    @Test
    @DisplayName("Deve retornar o Usuário quando encontrado pelo email")
    void deveRetornarUsuarioQuandoEncontradoPorEmail() {
        usuarioRepository.save(usuarioInativo);
        Usuario encontrado = usuarioRepository.findByEmail("bob@teste.com");

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getEmail()).isEqualTo("bob@teste.com");
    }

    @Test
    @DisplayName("Deve retornar null quando o email não for encontrado")
    void deveRetornarNuloQuandoEmailNaoForEncontrado() {
        Usuario encontrado = usuarioRepository.findByEmail("desconhecido@teste.com");
        assertThat(encontrado).isNull();
    }
}