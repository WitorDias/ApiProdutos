package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.exception.ProdutoNaoEncontradoException;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;


@Service
public class ProdutoConsultaService {

    private final ProdutoRepository produtoRepository;

    public ProdutoConsultaService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ProdutoNaoEncontradoException(id)
        );
    }
}
