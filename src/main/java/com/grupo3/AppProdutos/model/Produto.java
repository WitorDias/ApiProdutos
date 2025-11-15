package com.grupo3.AppProdutos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="produto_id")
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String sku;

    @Column(name = "criado_em",updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Boolean ativo;
}
