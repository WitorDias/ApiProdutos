CREATE TABLE IF NOT EXISTS tb_carrinho (
    carrinho_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    status_carrinho VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrinho_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(usuario_id) ON DELETE CASCADE
);

CREATE INDEX idx_carrinho_usuario_status ON tb_carrinho(usuario_id, status_carrinho);

