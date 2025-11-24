package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.auditoriaDTO.CarrinhoAuditDTO;
import com.grupo3.AppProdutos.dto.auditoriaDTO.ItemCarrinhoAuditDTO;
import com.grupo3.AppProdutos.dto.auditoriaDTO.ItemPedidoAuditDTO;
import com.grupo3.AppProdutos.dto.auditoriaDTO.PedidoAuditDTO;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoDTO.CarrinhoResponse;
import com.grupo3.AppProdutos.exception.*;
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
    private final AuditService auditService;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemCarrinhoRepository itemCarrinhoRepository, UsuarioRepository usuarioRepository, ProdutoConsultaService produtoConsultaService, CarrinhoMapper carrinhoMapper, EstoqueService estoqueService, PedidoService pedidoService, AuditService auditService) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoConsultaService = produtoConsultaService;
        this.carrinhoMapper = carrinhoMapper;
        this.estoqueService = estoqueService;
        this.pedidoService = pedidoService;
        this.auditService = auditService;
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
                throw new EstoqueInsuficienteException(estoque.getQuantidade(), quantidadeTotal);
            }

            ItemCarrinhoAuditDTO estadoAnterior = toItemAuditDTO(item);

            item.setQuantidade(quantidadeTotal);
            itemCarrinhoRepository.save(item);

            auditService.registrar("ItemCarrinho", item.getId(), TipoOperacao.UPDATE, estadoAnterior, toItemAuditDTO(item));

        } else {
            if (request.quantidade() > estoque.getQuantidade()) {
                throw new EstoqueInsuficienteException(estoque.getQuantidade(), request.quantidade());
            }

            ItemCarrinho novoItem = ItemCarrinho.builder()
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(request.quantidade())
                    .capturaPreco(produto.getPreco())
                    .build();
            itemCarrinhoRepository.save(novoItem);
            auditService.registrar("ItemCarrinho", novoItem.getId(), TipoOperacao.CREATE, null, toItemAuditDTO(novoItem));
        }

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        auditService.registrar("Carrinho", carrinho.getId(), TipoOperacao.UPDATE, null, toAuditDTO(carrinho));
        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse atualizarItem(Long usuarioId, Long produtoId, AtualizarItemCarrinhoRequest request) {
        validarQuantidade(request.quantidade());

        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(ItemNaoEstaNoCarrinhoException::new);

        var estoque = estoqueService.buscarEstoquePorProdutoId(produto.getId());

        if (request.quantidade() > estoque.getQuantidade()) {
            throw new EstoqueInsuficienteException(estoque.getQuantidade(), request.quantidade());
        }

        ItemCarrinhoAuditDTO estadoAnterior = toItemAuditDTO(item);
        item.setQuantidade(request.quantidade());
        itemCarrinhoRepository.save(item);

        auditService.registrar("ItemCarrinho", item.getId(), TipoOperacao.UPDATE, estadoAnterior, toItemAuditDTO(item));

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse removerItem(Long usuarioId, Long produtoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no carrinho"));

        ItemCarrinhoAuditDTO estadoAnterior = toItemAuditDTO(item);

        carrinho.getItens().remove(item);
        itemCarrinhoRepository.delete(item);
        itemCarrinhoRepository.flush();

        auditService.registrar("ItemCarrinho", estadoAnterior.id(), TipoOperacao.DELETE, estadoAnterior, null);
        auditService.registrar("Carrinho", carrinho.getId(), TipoOperacao.UPDATE, null, toAuditDTO(carrinho));

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public void limparCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum carrinho ativo encontrado"));

        for (ItemCarrinho item : carrinho.getItens()) {
            auditService.registrar("ItemCarrinho", item.getId(), TipoOperacao.DELETE, toItemAuditDTO(item), null);
        }

        itemCarrinhoRepository.deleteAll(carrinho.getItens());
    }

    @Transactional
    public Long finalizarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        if (carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException();
        }

        // Captura estado anterior do carrinho antes de finalizar
        CarrinhoAuditDTO carrinhoAnterior = toAuditDTO(carrinho);

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

        auditService.registrar("Carrinho", carrinho.getId(), TipoOperacao.UPDATE, carrinhoAnterior, toAuditDTO(carrinho));
        auditService.registrar("Pedido", pedido.getId(), TipoOperacao.CREATE, null, toPedidoAuditDTO(pedido));

        return pedido.getId();
    }

    @Transactional
    public void cancelarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        CarrinhoAuditDTO estadoAnterior = toAuditDTO(carrinho);

        carrinho.setStatusCarrinho(StatusCarrinho.CANCELADO);
        carrinhoRepository.save(carrinho);

        auditService.registrar("Carrinho", carrinho.getId(), TipoOperacao.UPDATE, estadoAnterior, toAuditDTO(carrinho));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findByIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
    }

    private Carrinho buscarOuCriarCarrinhoAtivo(Usuario usuario) {
        Optional<Carrinho> carrinhoExistente = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO);

        if (carrinhoExistente.isPresent()) {
            return carrinhoExistente.get();
        }

        if (carrinhoRepository.existsByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)) {
            throw new CarrinhoAtivoJaExisteException();
        }

        Carrinho novoCarrinho = Carrinho.builder()
                .usuario(usuario)
                .statusCarrinho(StatusCarrinho.ABERTO)
                .build();


        Carrinho salvo = carrinhoRepository.save(novoCarrinho);
        auditService.registrar("Carrinho", salvo.getId(), TipoOperacao.CREATE, null, toAuditDTO(salvo));

        return salvo;
    }


    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new QuantidadeInvalidaException("A quantidade deve ser maior que 0");
        }
    }

    private CarrinhoAuditDTO toAuditDTO(Carrinho carrinho) {
        return new CarrinhoAuditDTO(
                carrinho.getId(),
                carrinho.getUsuario().getId(),
                carrinho.getUsuario().getEmail(),
                carrinho.getStatusCarrinho(),
                carrinho.getCriadoEm(),
                carrinho.getAtualizadoEm(),
                carrinho.getItens().stream()
                        .map(this::toItemAuditDTO)
                        .toList()
        );
    }

    private ItemCarrinhoAuditDTO toItemAuditDTO(ItemCarrinho item) {
        return new ItemCarrinhoAuditDTO(
                item.getId(),
                item.getCarrinho().getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getCapturaPreco()
        );
    }

    private PedidoAuditDTO toPedidoAuditDTO(Pedido pedido) {
        return new PedidoAuditDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getEmail(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                pedido.getItens().stream()
                        .map(this::toItemPedidoAuditDTO)
                        .toList()
        );
    }

    private ItemPedidoAuditDTO toItemPedidoAuditDTO(ItemPedido item) {
        return new ItemPedidoAuditDTO(
                item.getId(),
                item.getPedido().getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getPrecoTotal()
        );
    }

}

