package com.grupo3.AppProdutos.auditoria;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidade;
    private String entidadeId;

    @Enumerated(EnumType.STRING)
    private TipoOperacao operacao;

    @Column(columnDefinition = "TEXT")
    private String dadoAnterior;

    @Column(columnDefinition = "TEXT")
    private String dadoPosterior;

    private String usuario;
    private LocalDateTime timestamp;

}
