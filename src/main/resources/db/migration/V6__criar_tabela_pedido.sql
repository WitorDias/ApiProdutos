CREATE TABLE tb_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    criado_em DATETIME NOT NULL,
    atualizado_em DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    valor_total DECIMAL(19,2) NOT NULL,

    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id)
        REFERENCES tb_usuario(usuario_id)
);
