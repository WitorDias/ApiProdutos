package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.AdicionarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.AtualizarItemCarrinhoRequest;
import com.grupo3.AppProdutos.dto.CarrinhoResponse;
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
    private final MovimentoEstoqueService movimentoEstoqueService;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, ItemCarrinhoRepository itemCarrinhoRepository, UsuarioRepository usuarioRepository, ProdutoConsultaService produtoConsultaService, CarrinhoMapper carrinhoMapper, EstoqueService estoqueService, MovimentoEstoqueService movimentoEstoqueService) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoConsultaService = produtoConsultaService;
        this.carrinhoMapper = carrinhoMapper;
        this.estoqueService = estoqueService;
        this.movimentoEstoqueService = movimentoEstoqueService;
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

            item.setQuantidade(quantidadeTotal);
            itemCarrinhoRepository.save(item);
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
        }

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(CarrinhoNaoEncontradoException::new);

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

        item.setQuantidade(request.quantidade());
        itemCarrinhoRepository.save(item);

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public CarrinhoResponse removerItem(Long usuarioId, Long produtoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Produto produto = produtoConsultaService.buscarProdutoPorId(produtoId);

        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        ItemCarrinho item = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(ItemNaoEstaNoCarrinhoException::new);

        carrinho.getItens().remove(item);

        itemCarrinhoRepository.delete(item);

        itemCarrinhoRepository.flush();

        carrinho = carrinhoRepository.findById(carrinho.getId())
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        return carrinhoMapper.toResponse(carrinho);
    }

    @Transactional
    public void limparCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = buscarOuCriarCarrinhoAtivo(usuario);

        itemCarrinhoRepository.deleteAll(carrinho.getItens());
    }

    @Transactional
    public void finalizarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        if (carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException();
        }

        for (ItemCarrinho item : carrinho.getItens()) {
            movimentoEstoqueService.registrarSaida(item.getProduto().getId(), item.getQuantidade());
        }

        carrinho.setStatusCarrinho(StatusCarrinho.FINALIZADO);
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void cancelarCarrinho(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuarioAndStatusCarrinho(usuario, StatusCarrinho.ABERTO)
                .orElseThrow(CarrinhoNaoEncontradoException::new);

        carrinho.setStatusCarrinho(StatusCarrinho.CANCELADO);
        carrinhoRepository.save(carrinho);
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
        return carrinhoRepository.save(novoCarrinho);
    }


    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoException("A quantidade deve ser maior que 0");
        }
    }
}

