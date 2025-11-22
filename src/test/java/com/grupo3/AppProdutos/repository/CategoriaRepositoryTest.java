package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaRoot;
    private Categoria subcategoria;
    private Categoria outraRoot;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        categoriaRoot = categoriaRepository.save(Categoria.builder()
                .nome("Eletrônicos")
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        subcategoria = categoriaRepository.save(Categoria.builder()
                .nome("Smartphones")
                .parent(categoriaRoot)
                .criadoEm(now.plusMinutes(1))
                .atualizadoEm(now.plusMinutes(1))
                .build());

        outraRoot = categoriaRepository.save(Categoria.builder()
                .nome("Livros")
                .criadoEm(now.plusMinutes(2))
                .atualizadoEm(now.plusMinutes(2))
                .build());
    }


    @Test
    @DisplayName("Deve retornar true se a subcategoria existe sob o pai específico")
    void deveRetornarTrueSeSubcategoriaExisteComPaiEspecifico() {
        boolean existe = categoriaRepository.existsByNomeAndParent("Smartphones", categoriaRoot);
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se a subcategoria existe, mas sob o pai incorreto")
    void deveRetornarFalseSeSubcategoriaExisteComPaiIncorreto() {
        boolean existe = categoriaRepository.existsByNomeAndParent("Smartphones", outraRoot);
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se a categoria não existe sob o pai específico")
    void deveRetornarFalseSeCategoriaNaoExisteSobPai() {
        boolean existe = categoriaRepository.existsByNomeAndParent("Tablets", categoriaRoot);
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false ao buscar categoria raiz com parent não nulo")
    void deveRetornarFalseAoBuscarCategoriaRaizComParentNaoNulo() {
        boolean existe = categoriaRepository.existsByNomeAndParent("Eletrônicos", categoriaRoot);
        assertThat(existe).isFalse();
    }


    @Test
    @DisplayName("Deve retornar true se a categoria raiz existe pelo nome")
    void deveRetornarTrueSeCategoriaRaizExiste() {
        boolean existe = categoriaRepository.existsByNomeAndParentIsNull("Livros");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se a categoria raiz não existe pelo nome")
    void deveRetornarFalseSeCategoriaRaizNaoExiste() {
        boolean existe = categoriaRepository.existsByNomeAndParentIsNull("Móveis");
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se o nome for de uma subcategoria (Parent não é Null)")
    void deveRetornarFalseSeNomeForDeSubcategoria() {
        boolean existe = categoriaRepository.existsByNomeAndParentIsNull("Smartphones");
        assertThat(existe).isFalse();
    }
}