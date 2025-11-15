package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProdutoConsultaService {

    private final ProdutoRepository produtoRepository;

    public ProdutoConsultaService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findProdutoById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado")
        );
    }
}
