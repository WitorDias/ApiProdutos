package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByNome(String nome);

    boolean existsByEmail(String email);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

}
