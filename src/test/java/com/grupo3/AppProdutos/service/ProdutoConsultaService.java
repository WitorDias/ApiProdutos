package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ProdutoConsultaService.class)
class ProdutoConsultaServiceTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoConsultaService produtoConsultaService;

    private Produto criarProdutoAtivo() {
        return Produto.builder()
                .nome("Monitor Gamer")
                .descricao("144Hz")
                .preco(BigDecimal.valueOf(1200.00))
                .sku("MON-001")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .ativo(true)
                .build();
    }

    private Produto criarProdutoInativo() {
        return Produto.builder()
                .nome("HD 1TB")
                .descricao("SATA")
                .preco(BigDecimal.valueOf(300.00))
                .sku("HD-123")
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .ativo(false)
                .build();
    }

    @Test
    @DisplayName("Deve buscar produto ativo por ID com sucesso")
    void deveBuscarProdutoAtivoPorId() {
        Produto salvo = produtoRepository.save(criarProdutoAtivo());

        Produto encontrado = produtoConsultaService.buscarProdutoPorId(salvo.getId());

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getSku()).isEqualTo("MON-001");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        Produto salvo = produtoRepository.save(criarProdutoInativo());

        assertThrows(ResponseStatusException.class, () -> {
            produtoConsultaService.buscarProdutoPorId(salvo.getId());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        assertThrows(ResponseStatusException.class, () -> {
            produtoConsultaService.buscarProdutoPorId(999L);
        });
    }
}
