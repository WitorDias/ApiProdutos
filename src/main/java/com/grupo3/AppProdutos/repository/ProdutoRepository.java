package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findProdutoById(Long id);
    Optional<Produto> findBySku(String string);

}

