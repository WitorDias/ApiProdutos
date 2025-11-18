package com.grupo3.AppProdutos.model;

import com.grupo3.AppProdutos.model.enums.TipoMovimento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Integer quantidade;

    @Column(name = "tipo_movimento")
    @Enumerated(EnumType.STRING)
    private TipoMovimento tipoMovimento;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

}
