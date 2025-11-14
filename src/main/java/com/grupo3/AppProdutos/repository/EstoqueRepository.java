package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Estoque;
import com.grupo3.AppProdutos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProduto(Produto produto);

    @Modifying
    @Query("""
            DELETE FROM Estoque e
            WHERE e.produto.id = :produtoId
            """)
    void deleteByProdutoId(Long id);
}
