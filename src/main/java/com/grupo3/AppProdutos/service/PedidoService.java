package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.ItemPedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoRequest;
import com.grupo3.AppProdutos.dto.PedidoResponse;
import com.grupo3.AppProdutos.mapper.PedidoMapper;
import com.grupo3.AppProdutos.model.ItemPedido;
import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.StatusPedido;
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
                () -> new RuntimeException("Usuário não encontrado")
        );

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        BigDecimal total = BigDecimal.ZERO;

        for(ItemPedidoRequest itemRequest : pedidoRequest.itens()){
            var produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(
                            () -> new RuntimeException("Produto não encontrado.")
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
                        () -> new RuntimeException("Pedido não encontrado")
                );

        return PedidoMapper.toResponse(pedido);
    }

    public List<PedidoResponse> buscarPedidoPorUsuario(Long usuarioId){

        var usuario = usuarioRepository.findByIdAndAtivoTrue(usuarioId).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado")
        );

        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());

        return pedidos.stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Pedido já está cancelado e não pode ser alterado.");
        }

        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new RuntimeException("Pedido já está finalizado e não pode ser alterado.");
        }

        if (novoStatus == StatusPedido.CONFIRMADO) {
            baixarEstoqueRegistrandoMovimentos(pedido);
        }

        pedido.setStatus(novoStatus);
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
}
