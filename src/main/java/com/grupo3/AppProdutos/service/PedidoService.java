package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.PedidoDTO.ItemPedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoDTO.PedidoResponse;
import com.grupo3.AppProdutos.exception.*;
import com.grupo3.AppProdutos.mapper.PedidoMapper;
import com.grupo3.AppProdutos.model.ItemPedido;
import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import com.grupo3.AppProdutos.repository.PedidoRepository;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueService movimentoEstoqueService;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, MovimentoEstoqueService movimentoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.movimentoEstoqueService = movimentoEstoqueService;
    }

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest pedidoRequest){

        var usuario = usuarioRepository.findByIdAndAtivoTrue(pedidoRequest.usuarioId()).orElseThrow(
                () -> new UsuarioNaoEncontradoException(pedidoRequest.usuarioId())
        );

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        BigDecimal total = BigDecimal.ZERO;

        for(ItemPedidoRequest itemRequest : pedidoRequest.itens()){
            var produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(
                            () -> new ProdutoNaoEncontradoException(itemRequest.produtoId())
                    );

            ItemPedido itemPedido = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemRequest.quantidade())
                    .precoUnitario(produto.getPreco())
                    .precoTotal(produto.getPreco().multiply(BigDecimal.valueOf(itemRequest.quantidade())))
                    .build();

            pedido.getItens().add(itemPedido);
            total = total.add(itemPedido.getPrecoTotal());
        }
        pedido.setValorTotal(total);
        pedidoRepository.save(pedido);
        return PedidoMapper.toResponse(pedido);
    }

    public PedidoResponse buscarPedidoPorId(Long id) {
        var pedido = pedidoRepository.findById(id)
                .orElseThrow(
                        () -> new PedidoNaoEncontradoException(id)
                );

        return PedidoMapper.toResponse(pedido);
    }

    public List<PedidoResponse> buscarPedidoPorUsuario(Long usuarioId){

        var usuario = usuarioRepository.findByIdAndAtivoTrue(usuarioId).orElseThrow(
                () -> new UsuarioNaoEncontradoException(usuarioId)
        );

        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());

        return pedidos.stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(
                        () -> new PedidoNaoEncontradoException(id)
                );

        StatusPedido statusAtual = pedido.getStatus();

        validarTransicao(statusAtual, novoStatus);
        pedido.setStatus(novoStatus);

        if (statusAtual == StatusPedido.NOVO && novoStatus == StatusPedido.CONFIRMADO) {
            baixarEstoqueRegistrandoMovimentos(pedido);
        }

        if (statusAtual == StatusPedido.CONFIRMADO && novoStatus == StatusPedido.CANCELADO) {
            desfazerBaixaDeEstoque(pedido);
        }

        pedidoRepository.save(pedido);

        return PedidoMapper.toResponse(pedido);
    }


    private void baixarEstoqueRegistrandoMovimentos(Pedido pedido) {

        for (ItemPedido item : pedido.getItens()) {

            movimentoEstoqueService.registrarSaida(
                    item.getProduto().getId(),
                    item.getQuantidade()
            );
        }
    }

    private void desfazerBaixaDeEstoque(Pedido pedido) {

        for (ItemPedido item : pedido.getItens()) {
            movimentoEstoqueService.registrarEntrada(
                    item.getProduto().getId(),
                    item.getQuantidade()
            );
        }
    }

    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    private void validarTransicao(StatusPedido atual, StatusPedido novoStatus) {

        if (atual == novoStatus) {
            throw new StatusJaDefinidoException(atual);
        }

        switch (atual) {
            case NOVO -> {
                if (!(novoStatus == StatusPedido.CONFIRMADO ||
                        novoStatus == StatusPedido.CANCELADO)) {
                    throw new TransicaoStatusInvalidaException(atual, novoStatus);
                }
            }

            case CONFIRMADO -> {
                if (!(novoStatus == StatusPedido.CANCELADO ||
                        novoStatus == StatusPedido.ENVIADO)) {
                    throw new TransicaoStatusInvalidaException(atual, novoStatus);
                }
            }

            case ENVIADO -> {
                if (novoStatus != StatusPedido.ENTREGUE) {
                    throw new TransicaoStatusInvalidaException(atual, novoStatus);
                }
            }

            case ENTREGUE -> {
                if (novoStatus != StatusPedido.FINALIZADO) {
                    throw new TransicaoStatusInvalidaException(atual, novoStatus);
                }
            }

            case CANCELADO, FINALIZADO -> {
                throw new PedidoImutavelException(atual);
            }
        }
    }


}
