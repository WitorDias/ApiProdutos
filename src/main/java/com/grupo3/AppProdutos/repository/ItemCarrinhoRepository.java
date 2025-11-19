package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Carrinho;
import com.grupo3.AppProdutos.model.ItemCarrinho;
import com.grupo3.AppProdutos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    Optional<ItemCarrinho> findByCarrinhoAndProduto(Carrinho carrinho, Produto produto);

}
