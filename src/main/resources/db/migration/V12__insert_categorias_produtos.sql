INSERT INTO tb_categoria (nome, parent_id, criado_em, atualizado_em)
VALUES
('Eletrônicos', NULL, NOW(), NOW()),
('Informática', NULL, NOW(), NOW()),
('Acessórios', NULL, NOW(), NOW()),
('Livros', NULL, NOW(), NOW()),
('Moda', NULL, NOW(), NOW()),
('Esportes', NULL, NOW(), NOW()),
('Casa e Cozinha', NULL, NOW(), NOW()),
('Periféricos', 2, NOW(), NOW()),      -- filho de Informática
('Smartphones', 1, NOW(), NOW()),      -- filho de Eletrônicos
('Eletroportáteis', 7, NOW(), NOW());  -- filho de Casa e Cozinha

INSERT INTO tb_produto (nome, descricao, preco, sku, categoria_id, criado_em, atualizado_em, ativo)
VALUES
('Notebook Dell Inspiron', 'Notebook com 16GB RAM e SSD 512GB', 4500.00, 'SKU-NTB-001', 2, NOW(), NOW(), TRUE),
('Teclado Mecânico RGB', 'Teclado mecânico switch blue', 350.00, 'SKU-TCL-002', 8, NOW(), NOW(), TRUE),
('Mouse Gamer 7200DPI', 'Mouse com alta precisão', 120.00, 'SKU-MSE-003', 8, NOW(), NOW(), TRUE),
('Smartphone Samsung A54', 'Smartphone com 256GB de armazenamento', 2200.00, 'SKU-SMP-004', 9, NOW(), NOW(), TRUE),
('Air Fryer 4L', 'Fritadeira elétrica sem óleo', 380.00, 'SKU-EPT-005', 10, NOW(), NOW(), TRUE),
('Camiseta Básica Preta', 'Camiseta 100% algodão', 49.90, 'SKU-CMS-006', 5, NOW(), NOW(), TRUE),
('Chuteira Nike', 'Chuteira profissional de campo', 299.00, 'SKU-CTR-007', 6, NOW(), NOW(), TRUE),
('Livro Clean Code', 'Código limpo para programação profissional', 89.90, 'SKU-LVR-008', 4, NOW(), NOW(), TRUE),
('Fone Bluetooth JBL', 'Fone de ouvido sem fio', 250.00, 'SKU-FON-009', 1, NOW(), NOW(), TRUE),
('Processador Ryzen 5 5600G', 'Processador AMD com 6 núcleos', 950.00, 'SKU-CPU-010', 2, NOW(), NOW(), TRUE);

INSERT INTO tb_estoque (produto_id, quantidade, criado_em, atualizado_em)
VALUES
(1, 20, NOW(), NOW()),
(2, 50, NOW(), NOW()),
(3, 40, NOW(), NOW()),
(4, 30, NOW(), NOW()),
(5, 25, NOW(), NOW()),
(6, 80, NOW(), NOW()),
(7, 15, NOW(), NOW()),
(8, 60, NOW(), NOW()),
(9, 45, NOW(), NOW()),
(10, 22, NOW(), NOW());
