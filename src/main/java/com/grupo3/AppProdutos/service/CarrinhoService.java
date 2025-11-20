package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.CarrinhoDTO.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.CarrinhoResponse;
import com.grupo3.AppProdutos.mapper.CarrinhoMapper;
import com.grupo3.AppProdutos.model.*;
import com.grupo3.AppProdutos.model.enums.StatusCarrinho;
import com.grupo3.AppProdutos.repository.CarrinhoRepository;
import com.grupo3.AppProdutos.repository.ItemCarrinhoRepository;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoConsultaService produtoConsultaService;
    private final CarrinhoMapper carrinhoMapper;
    private final EstoqueService estoqueService;
    private final PedidoService pedidoService;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemCarrinhoRepository itemCarrinhoRepository, UsuarioRepository usuarioRepository, ProdutoConsultaService produtoConsultaService, CarrinhoMapper carrinhoMapper, EstoqueService estoqueService, PedidoService pedidoService) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoConsultaService = produtoConsultaService;
        this.carrinhoMapper = carrinhoMapper;
        this.estoqueService = estoqueService;
        this.pedidoService = pedidoService;
    }

    public CarrinhoResponse buscarCarrinhoAtivo(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = buscarOuCriarCarrinhoAtivo(usuario);
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse adicionarItem(Long usuarioId, AdicionarItemCarrinhoRequest request) {
        validarQuantidade(request.quantidade());

        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(request.produtoId());
        Carrinho carrinho = buscarOuCriarCarrinhoAtivo(usuario);

        var estoque = estoqueService.buscarEstoquePorProdutoId(produto.getId());

        var itemExistente = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto);

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            Integer quantidadeTotal = item.getQuantidade() + request.quantidade();

            if (quantidadeTotal > estoque.getQuantidade()) {
                throw new IllegalArgumentException(
                    String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d",
                        estoque.getQuantidade(), quantidadeTotal)
                );
            }

            item.setQuantidade(quantidadeTotal);
            itemCarrinhoRepository.save(item);
        } else {
            if (request.quantidade() > estoque.getQuantidade()) {
                throw new IllegalArgumentException(
                    String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d",
                        estoque.getQuantidade(), request.quantidade())
                );
            }

            ItemCarrinho novoItem = ItemCarrinho.builder()
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(request.quantidade())
                    .capturaPreco(produto.getPreco())
                    .build();
            itemCarrinhoRepository.save(novoItem);
        }

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse atualizarItem(Long usuarioId, Long produtoId, AtualizarItemCarrinhoRequest request) {
        validarQuantidade(request.quantidade());

        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no carrinho"));

        var estoque = estoqueService.buscarEstoquePorProdutoId(produto.getId());

        if (request.quantidade() > estoque.getQuantidade()) {
            throw new IllegalArgumentException(
                String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d",
                    estoque.getQuantidade(), request.quantidade())
            );
        }

        item.setQuantidade(request.quantidade());
        itemCarrinhoRepository.save(item);

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse removerItem(Long usuarioId, Long produtoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no carrinho"));

        itemCarrinhoRepository.delete(item);

        itemCarrinhoRepository.flush();

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public void limparCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        itemCarrinhoRepository.deleteAll(carrinho.getItens());
    }

    @Transactional
    public Long finalizarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("Não é possível finalizar um carrinho vazio");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            ItemPedido itemPedido = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(itemCarrinho.getProduto())
                    .quantidade(itemCarrinho.getQuantidade())
                    .precoUnitario(itemCarrinho.getCapturaPreco())
                    .precoTotal(itemCarrinho.getCapturaPreco().multiply(java.math.BigDecimal.valueOf(itemCarrinho.getQuantidade())))
                    .build();
            pedido.getItens().add(itemPedido);
        }

        java.math.BigDecimal total = pedido.getItens().stream()
                .map(ItemPedido::getPrecoTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        pedido.setValorTotal(total);

        pedido = pedidoService.salvarPedido(pedido);

        carrinho.setStatusCarrinho(StatusCarrinho.FINALIZADO);
        carrinhoRepository.save(carrinho);

        return pedido.getId();
    }

    @Transactional
    public void cancelarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        carrinho.setStatusCarrinho(StatusCarrinho.CANCELADO);
        carrinhoRepository.save(carrinho);
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + usuarioId));
    }

    private Carrinho buscarOuCriarCarrinhoAtivo(Usuario usuario) {
        Optional<Carrinho> carrinhoExistente = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO);

        if (carrinhoExistente.isPresent()) {
            return carrinhoExistente.get();
        }

        if (carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)) {
            throw new IllegalStateException("Usuário já possui um carrinho ativo");
        }

        Carrinho novoCarrinho = Carrinho.builder()
                .usuario(usuario)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .build();
        return carrinhoRepository.save(novoCarrinho);
    }


    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que 0");
        }
    }
}

