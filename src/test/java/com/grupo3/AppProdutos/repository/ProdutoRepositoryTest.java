package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto criarProdutoAtivo() {
        return Produto.builder()
                .nome("Teclado Gamer")
                .descricao("Teclado mecânico RGB")
                .preco(BigDecimal.valueOf(250.00))
                .sku("TEC-001")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .ativo(true)
                .build();
    }

    private Produto criarProdutoInativo() {
        return Produto.builder()
                .nome("Mouse Gamer")
                .descricao("Mouse RGB")
                .preco(BigDecimal.valueOf(150.00))
                .sku("MOU-123")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .ativo(false)
                .build();
    }

    @Test
    @DisplayName("Deve salvar e recuperar um produto ativo pelo ID")
    void deveSalvarERecuperarProdutoAtivoPorId() {
        Produto produto = criarProdutoAtivo();
        Produto salvo = produtoRepository.save(produto);

        Optional<Produto> encontrado = produtoRepository.findByIdAndAtivoTrue(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Teclado Gamer");
    }

    @Test
    @DisplayName("Não deve retornar produto inativo ao buscar por ID")
    void naoDeveRetornarProdutoInativoPorId() {
        Produto inativo = criarProdutoInativo();
        Produto salvo = produtoRepository.save(inativo);

        Optional<Produto> encontrado = produtoRepository.findByIdAndAtivoTrue(salvo.getId());

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve listar apenas produtos ativos")
    void deveListarSomenteProdutosAtivos() {
        produtoRepository.save(criarProdutoAtivo());
        produtoRepository.save(criarProdutoInativo());

        var lista = produtoRepository.findAllByAtivoTrue();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getSku()).isEqualTo("TEC-001");
    }

    @Test
    @DisplayName("Deve encontrar um produto pelo SKU")
    void deveEncontrarProdutoPeloSku() {
        Produto produto = criarProdutoAtivo();
        produtoRepository.save(produto);

        Optional<Produto> encontrado = produtoRepository.findBySku("TEC-001");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getSku()).isEqualTo("TEC-001");
    }

    @Test
    @DisplayName("Deve retornar vazio quando SKU não existir")
    void deveRetornarVazioQuandoSkuNaoExistir() {
        Optional<Produto> encontrado = produtoRepository.findBySku("NAO-EXISTE");

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Não deve permitir salvar dois produtos com o mesmo SKU, mesmo se um estiver inativo")
    void naoDevePermitirDuplicidadeSkuMesmoComProdutoInativo() {
        Produto inativo = criarProdutoInativo();
        produtoRepository.save(inativo);

        Produto novo = criarProdutoAtivo();
        novo.setSku("MOU-123");

        assertThrows(Exception.class, () -> {
            produtoRepository.saveAndFlush(novo);
        });
    }
}
