package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EstoqueRepositoryTest {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Produto produtoA;
    private Produto produtoB;
    private Estoque estoqueA;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();


        Categoria categoriaBase = categoriaRepository.save(Categoria.builder()
                .nome("Testes")
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoA = produtoRepository.save(Produto.builder()
                .nome("Produto Estoque A")
                .descricao("Desc A")
                .preco(new BigDecimal("10.00"))
                .sku("SKU-A")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoB = produtoRepository.save(Produto.builder()
                .nome("Produto Estoque B")
                .descricao("Desc B")
                .preco(new BigDecimal("20.00"))
                .sku("SKU-B")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        estoqueA = Estoque.builder()
                .produto(produtoA)
                .quantidade(10)
                .criadoEm(now)
                .atualizadoEm(now)
                .build();

        estoqueA = estoqueRepository.save(estoqueA);
    }


    @Test
    @DisplayName("Deve encontrar o Estoque quando buscado pelo Objeto Produto")
    void deveEncontrarEstoquePorProduto() {
        Optional<Estoque> encontrado = estoqueRepository.findByProduto(produtoA);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getProduto().getNome()).isEqualTo("Produto Estoque A");
        assertThat(encontrado.get().getQuantidade()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para um Produto que não tem Estoque associado")
    void deveRetornarVazioParaProdutoSemEstoque() {

        Optional<Estoque> encontrado = estoqueRepository.findByProduto(produtoB);

        assertThat(encontrado).isNotPresent();
    }


    @Test
    @DisplayName("Deve deletar o Estoque corretamente usando o ID do Produto")
    @Transactional
    void deveDeletarEstoquePorIdProduto() {
        Long produtoIdParaDeletar = produtoA.getId();

        assertThat(estoqueRepository.findByProduto(produtoA)).isPresent();

        estoqueRepository.deleteByProdutoId(produtoIdParaDeletar);

        estoqueRepository.flush();

        Optional<Estoque> aposDelecao = estoqueRepository.findByProduto(produtoA);
        assertThat(aposDelecao).isNotPresent();
    }


    @Test
    @DisplayName("Deve salvar novo Estoque e gerar ID corretamente")
    void deveSalvarNovoEstoqueCorretamente() {
        LocalDateTime now = LocalDateTime.now();

        Produto novoProduto = produtoRepository.save(Produto.builder()
                .nome("Produto Novo Estoque")
                .descricao("Novo")
                .preco(new BigDecimal("5.00"))
                .sku("SKU-NEW")
                .categoria(produtoA.getCategoria())
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        Estoque novoEstoque = Estoque.builder()
                .produto(novoProduto)
                .quantidade(50)
                .criadoEm(now.plusSeconds(1))
                .atualizadoEm(now.plusSeconds(1))
                .build();

        Estoque salvo = estoqueRepository.save(novoEstoque);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getQuantidade()).isEqualTo(50);
        assertThat(salvo.getProduto().getSku()).isEqualTo("SKU-NEW");
    }
}