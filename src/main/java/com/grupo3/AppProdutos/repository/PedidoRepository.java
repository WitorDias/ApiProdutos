package com.grupo3.AppProdutos.repository;

import com.grupo3.AppProdutos.model.Pedido;
import com.grupo3.AppProdutos.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByStatus(StatusPedido status);

}
