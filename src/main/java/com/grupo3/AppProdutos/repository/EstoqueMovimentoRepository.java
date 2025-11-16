package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.MovimentoEstoque;
import com.grupo3.AppProdutos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueMovimentoRepository extends JpaRepository<MovimentoEstoque, Long> {

    List<MovimentoEstoque> findByProduto_Id(Long produtoId);

    @Modifying
    @Query("""
            DELETE FROM MovimentoEstoque m 
            WHERE m.produto = :produto
            """)
    void deleteByProduto(Produto produto);
}
