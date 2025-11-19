CREATE TABLE tb_item_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(19,2) NOT NULL,
    preco_total DECIMAL(19,2) NOT NULL,

    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id)
        REFERENCES tb_pedido(id),

    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id)
        REFERENCES produto(produto_id)
);
