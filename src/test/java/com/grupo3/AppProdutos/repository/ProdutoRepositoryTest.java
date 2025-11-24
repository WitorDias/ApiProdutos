package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaBase;
    private Produto produtoAtivo;
    private Produto produtoInativo;
    private Produto produtoAtivo2;


    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        Categoria cat = Categoria.builder()
                .nome("Eletrônicos")
                .criadoEm(now)
                .atualizadoEm(now)
                .build();
        categoriaBase = categoriaRepository.save(cat);

        produtoAtivo = Produto.builder()
                .nome("Smartphone X")
                .descricao("Novo modelo")
                .preco(new BigDecimal("1500.00"))
                .sku("SMART-X-001")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now.minusDays(5))
                .atualizadoEm(now.minusDays(5))
                .build();

        produtoInativo = Produto.builder()
                .nome("Headphone Z")
                .descricao("Modelo antigo")
                .preco(new BigDecimal("300.00"))
                .sku("HEAD-Z-002")
                .categoria(categoriaBase)
                .ativo(false)
                .criadoEm(now.minusDays(10))
                .atualizadoEm(now.minusDays(10))
                .build();

        produtoAtivo2 = Produto.builder()
                .nome("Mousepad Pro")
                .descricao("Mousepad grande")
                .preco(new BigDecimal("80.00"))
                .sku("MP-PRO-003")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now.minusDays(1))
                .atualizadoEm(now.minusDays(1))
                .build();

        produtoRepository.save(produtoAtivo);
        produtoRepository.save(produtoInativo);
        produtoRepository.save(produtoAtivo2);
    }


    @Test
    @DisplayName("Deve encontrar produto ativo pelo ID e retornar Optional com valor")
    void deveEncontrarProdutoAtivoPorIdERetornarOptionalComValor() {
        Optional<Produto> encontrado = produtoRepository.findByIdAndAtivoTrue(produtoAtivo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Smartphone X");
        assertThat(encontrado.get().getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Não deve encontrar produto inativo pelo ID, retornando Optional vazio")
    void naoDeveEncontrarProdutoInativoPorIdRetornandoOptionalVazio() {
        Optional<Produto> encontrado = produtoRepository.findByIdAndAtivoTrue(produtoInativo.getId());

        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Não deve encontrar produto por ID inexistente")
    void naoDeveEncontrarProdutoPorIdInexistente() {
        Optional<Produto> encontrado = produtoRepository.findByIdAndAtivoTrue(999L);
        assertThat(encontrado).isNotPresent();
    }


    @Test
    @DisplayName("Deve retornar apenas produtos com ativo igual a true")
    void deveRetornarApenasProdutosAtivos() {
        List<Produto> produtosAtivos = produtoRepository.findAllByAtivoTrue();

        assertThat(produtosAtivos)
                .extracting(Produto::getNome)
                .contains(
                        "Smartphone X",
                        "Mousepad Pro",
                        "Processador Ryzen 5 5600G"
                );

        assertThat(produtosAtivos)
                .extracting(Produto::getAtivo)
                .containsOnly(true);
    }




    @Test
    @DisplayName("Deve retornar produto quando encontrado pelo SKU")
    void deveRetornarProdutoQuandoEncontradoPorSku() {
        Optional<Produto> encontrado = produtoRepository.findBySku("HEAD-Z-002");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Headphone Z");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando SKU não for encontrado")
    void deveRetornarOptionalVazioQuandoSkuNaoForEncontrado() {
        Optional<Produto> encontrado = produtoRepository.findBySku("SKU-INEXISTENTE-123");

        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar produtos ativos ao buscar por parte do nome (contains, ignore case)")
    void deveRetornarProdutosAtivosAoBuscarPorNome() {
        List<Produto> encontrados = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("pro");

        assertThat(encontrados).hasSize(2);
        assertThat(encontrados)
                .extracting(Produto::getNome)
                .containsExactlyInAnyOrder("Mousepad Pro", "Processador Ryzen 5 5600G");
    }


    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum produto corresponder ao nome informado")
    void deveRetornarListaVaziaQuandoNenhumProdutoCorresponderAoNomeInformado() {
        List<Produto> encontrados = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("inexistente");

        assertThat(encontrados).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar categoria inexistente")
    void deveRetornarListaVaziaAoBuscarCategoriaInexistente() {
        List<Produto> encontrados = produtoRepository.findByCategoriaNomeIgnoreCaseAndAtivoTrue("Categoria Inexistente");

        assertThat(encontrados).isEmpty();
    }

}