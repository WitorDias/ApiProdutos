package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.ItemPedido;
import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.Produto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ItemPedidoRepositoryTest {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Produto produtoBase;
    private Pedido pedidoBase;
    private ItemPedido itemPedidoSalvo;

    @BeforeEach
    public void configuracao() {
        LocalDateTime now = LocalDateTime.now();

        Usuario usuarioBase = usuarioRepository.save(Usuario.builder()
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .senha("senha123")
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        Categoria categoriaBase = categoriaRepository.save(Categoria.builder()
                .nome("Geral")
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        produtoBase = produtoRepository.save(Produto.builder()
                .nome("Caneta Esferográfica")
                .descricao("Caneta azul")
                .preco(new BigDecimal("2.50"))
                .sku("CAN-AZUL-001")
                .categoria(categoriaBase)
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now)
                .build());

        pedidoBase = pedidoRepository.save(Pedido.builder()
                .usuario(usuarioBase)
                .status(StatusPedido.NOVO)
                .criadoEm(now)
                .valorTotal(BigDecimal.ZERO)
                .build());

        ItemPedido item = ItemPedido.builder()
                .pedido(pedidoBase)
                .produto(produtoBase)
                .quantidade(5)
                .precoUnitario(produtoBase.getPreco())
                .build();

        itemPedidoSalvo = itemPedidoRepository.save(item);
    }

    @Test
    @DisplayName("Deve salvar ItemPedido e calcular precoTotal corretamente no PrePersist")
    void deveSalvarItemPedidoECalcularPrecoTotalCorretamenteNoPrePersist() {
        assertThat(itemPedidoSalvo).isNotNull();
        assertThat(itemPedidoSalvo.getId()).isNotNull();

        BigDecimal precoEsperado = new BigDecimal("12.50");

        assertThat(itemPedidoSalvo.getPrecoUnitario()).isEqualByComparingTo("2.50");
        assertThat(itemPedidoSalvo.getQuantidade()).isEqualTo(5);
        assertThat(itemPedidoSalvo.getPrecoTotal()).isEqualByComparingTo(precoEsperado);
    }

    @Test
    @DisplayName("Deve encontrar ItemPedido por ID")
    void deveEncontrarItemPedidoPorId() {
        Optional<ItemPedido> encontrado = itemPedidoRepository.findById(itemPedidoSalvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getProduto().getNome()).isEqualTo("Caneta Esferográfica");
        assertThat(encontrado.get().getPedido().getId()).isEqualTo(pedidoBase.getId());
    }

    @Test
    @DisplayName("Deve deletar ItemPedido por ID")
    void deveDeletarItemPedidoPorId() {
        Long idParaDeletar = itemPedidoSalvo.getId();
        itemPedidoRepository.deleteById(idParaDeletar);

        Optional<ItemPedido> encontrado = itemPedidoRepository.findById(idParaDeletar);
        assertThat(encontrado).isNotPresent();
    }
}