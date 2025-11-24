package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.enums.TipoMovimento;
import jakarta.transaction.Transactional;
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
public class EstoqueMovimentoRepositoryTest {

    @Autowired
    private EstoqueMovimentoRepository estoqueMovimentoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Produto produtoA;
    private Produto produtoB;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        Categoria categoriaBase = categoriaRepository.save(Categoria.builder()
                .nome("Testes Movimento")
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoA = produtoRepository.save(Produto.builder()
                .nome("Kit Escritório")
                .descricao("Kit A")
                .preco(new BigDecimal("10.00"))
                .sku("KT-A")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoB = produtoRepository.save(Produto.builder()
                .nome("Material Limpeza")
                .descricao("Kit B")
                .preco(new BigDecimal("20.00"))
                .sku("KT-B")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        estoqueMovimentoRepository.save(MovimentoEstoque.builder()
                .produto(produtoA)
                .quantidade(100)
                .tipoMovimento(TipoMovimento.ENTRADA)
                .criadoEm(now)
                .build());

        estoqueMovimentoRepository.save(MovimentoEstoque.builder()
                .produto(produtoA)
                .quantidade(5)
                .tipoMovimento(TipoMovimento.SAIDA)
                .criadoEm(now.plusMinutes(5))
                .build());

        estoqueMovimentoRepository.save(MovimentoEstoque.builder()
                .produto(produtoB)
                .quantidade(50)
                .tipoMovimento(TipoMovimento.ENTRADA)
                .criadoEm(now)
                .build());
    }

    @Test
    @DisplayName("Deve salvar MovimentoEstoque com sucesso e ID gerado")
    void deveSalvarMovimentoEstoqueComSucesso() {
        MovimentoEstoque novoMovimento = MovimentoEstoque.builder()
                .produto(produtoB)
                .quantidade(10)
                .tipoMovimento(TipoMovimento.SAIDA)
                .criadoEm(LocalDateTime.now())
                .build();

        MovimentoEstoque salvo = estoqueMovimentoRepository.save(novoMovimento);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getQuantidade()).isEqualTo(10);
        assertThat(salvo.getTipoMovimento()).isEqualTo(TipoMovimento.SAIDA);
    }

    @Test
    @DisplayName("Deve encontrar todos os movimentos de estoque pelo ID do produto")
    void deveEncontrarMovimentosPeloIdDoProduto() {
        List<MovimentoEstoque> movimentosA = estoqueMovimentoRepository.findByProduto_Id(produtoA.getId());

        assertThat(movimentosA).hasSize(2);
        assertThat(movimentosA).extracting(MovimentoEstoque::getTipoMovimento)
                .containsExactlyInAnyOrder(TipoMovimento.ENTRADA, TipoMovimento.SAIDA);
        assertThat(movimentosA).extracting(m -> m.getProduto().getId())
                .containsOnly(produtoA.getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para ID de produto sem movimentos")
    void deveRetornarListaVaziaParaProdutoSemMovimentos() {
        List<MovimentoEstoque> movimentos = estoqueMovimentoRepository.findByProduto_Id(999L);
        assertThat(movimentos).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar todos os movimentos associados a um produto")
    @Transactional
    void deveDeletarMovimentosPeloObjetoProduto() {
        Long idProdutoA = produtoA.getId();
        Long idProdutoB = produtoB.getId();

        List<MovimentoEstoque> antesDelecao = estoqueMovimentoRepository.findByProduto_Id(idProdutoA);
        assertThat(antesDelecao).hasSize(2);

        estoqueMovimentoRepository.deleteByProduto(produtoA);
        estoqueMovimentoRepository.flush();

        List<MovimentoEstoque> aposDelecaoA = estoqueMovimentoRepository.findByProduto_Id(idProdutoA);
        assertThat(aposDelecaoA).isEmpty();

        List<MovimentoEstoque> aposDelecaoB = estoqueMovimentoRepository.findByProduto_Id(idProdutoB);
        assertThat(aposDelecaoB).hasSize(1);
        assertThat(aposDelecaoB.getFirst().getProduto().getId()).isEqualTo(idProdutoB);
    }
}