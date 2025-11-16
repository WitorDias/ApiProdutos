package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNomeAndParent(String nome, Categoria parent);

    boolean existsByNomeAndParentIsNull(String nome);

}
