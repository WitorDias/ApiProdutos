package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Carrinho;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.ItemCarrinho;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.StatusCarrinho;
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
public class ItemCarrinhoRepositoryTest {


    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    private Produto produtoA;
    private Produto produtoB;
    private Carrinho carrinhoUser1;
    private Carrinho carrinhoUser2;
    private ItemCarrinho itemSalvo;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        Usuario usuario1 = usuarioRepository.save(Usuario.builder()
                .nome("User 1")
                .email("user1@carrinho.com")
                .senha("123")
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        Usuario usuario2 = usuarioRepository.save(Usuario.builder()
                .nome("User 2")
                .email("user2@carrinho.com")
                .senha("456")
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        Categoria categoriaBase = categoriaRepository.save(Categoria.builder()
                .nome("Cores")
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoA = produtoRepository.save(Produto.builder()
                .nome("Caneta Vermelha")
                .descricao("Caneta")
                .preco(new BigDecimal("5.00"))
                .sku("CV-001")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoB = produtoRepository.save(Produto.builder()
                .nome("Caneta Azul")
                .descricao("Caneta")
                .preco(new BigDecimal("3.00"))
                .sku("CA-002")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        carrinhoUser1 = carrinhoRepository.save(Carrinho.builder()
                .usuario(usuario1)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        carrinhoUser2 = carrinhoRepository.save(Carrinho.builder()
                .usuario(usuario2)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        ItemCarrinho item = ItemCarrinho.builder()
                .carrinho(carrinhoUser1)
                .produto(produtoA)
                .quantidade(2)
                .capturaPreco(produtoA.getPreco())
                .build();

        itemSalvo = itemCarrinhoRepository.save(item);

        itemCarrinhoRepository.save(ItemCarrinho.builder()
                .carrinho(carrinhoUser2)
                .produto(produtoB)
                .quantidade(1)
                .capturaPreco(produtoB.getPreco())
                .build());
    }


    @Test
    @DisplayName("Deve encontrar ItemCarrinho quando o Produto e Carrinho forem correspondentes")
    void deveEncontrarItemCarrinhoPorCarrinhoEProduto() {
        Optional<ItemCarrinho> encontrado = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinhoUser1, produtoA);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(itemSalvo.getId());
        assertThat(encontrado.get().getQuantidade()).isEqualTo(2);
        assertThat(encontrado.get().getCapturaPreco()).isEqualByComparingTo("5.00");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o Carrinho estiver incorreto")
    void deveRetornarVazioQuandoCarrinhoIncorreto() {

        Optional<ItemCarrinho> encontrado = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinhoUser2, produtoA);

        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o Produto estiver incorreto")
    void deveRetornarVazioQuandoProdutoIncorreto() {

        Optional<ItemCarrinho> encontrado = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinhoUser1, produtoB);

        assertThat(encontrado).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando nenhum item corresponder")
    void deveRetornarVazioQuandoNenhumItemCorresponder() {
        Produto produtoInexistente = Produto.builder().id(999L).build();
        Optional<ItemCarrinho> encontrado = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinhoUser1, produtoInexistente);

        assertThat(encontrado).isNotPresent();
    }


    @Test
    @DisplayName("Deve salvar ItemCarrinho com sucesso e ID gerado")
    void deveSalvarItemCarrinhoComSucesso() {
        ItemCarrinho novoItem = ItemCarrinho.builder()
                .carrinho(carrinhoUser2)
                .produto(produtoA)
                .quantidade(3)
                .capturaPreco(new BigDecimal("5.00"))
                .build();

        ItemCarrinho salvo = itemCarrinhoRepository.save(novoItem);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getQuantidade()).isEqualTo(3);
    }
}