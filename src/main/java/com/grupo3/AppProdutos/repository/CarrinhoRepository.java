package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Carrinho;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.StatusCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByUsuarioAndStatusCarrinho(Usuario usuario, StatusCarrinho statusCarrinho);

    boolean existsByUsuarioAndStatusCarrinho(Usuario usuario, StatusCarrinho statusCarrinho);
}
