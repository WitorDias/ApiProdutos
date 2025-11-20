CREATE TABLE IF NOT EXISTS tb_item_carrinho (
    item_carrinho_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrinho_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    captura_preco DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_carrinho_v9 FOREIGN KEY (carrinho_id) REFERENCES tb_carrinho(carrinho_id) ON DELETE CASCADE,
    CONSTRAINT fk_carrinho_item_produto_v9 FOREIGN KEY (produto_id) REFERENCES tb_produto(produto_id) ON DELETE RESTRICT,
    CONSTRAINT uk_carrinho_produto_v9 UNIQUE (carrinho_id, produto_id)
);

