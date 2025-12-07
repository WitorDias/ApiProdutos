INSERT INTO tb_categoria (nome, parent_id, criado_em, atualizado_em)
VALUES
('Eletrônicos', NULL, NOW(), NOW()),
('Informática', NULL, NOW(), NOW()),
('Acessórios', NULL, NOW(), NOW()),
('Livros', NULL, NOW(), NOW()),
('Moda', NULL, NOW(), NOW()),
('Esportes', NULL, NOW(), NOW()),
('Casa e Cozinha', NULL, NOW(), NOW()),
('Celulares', NULL, NOW(), NOW()),
('Games', NULL, NOW(), NOW()),
('Beleza e Saúde', NULL, NOW(), NOW());

INSERT INTO tb_produto (nome, descricao, preco, sku, categoria_id, criado_em, atualizado_em, ativo)
VALUES
('Notebook Dell Inspiron 15', '16GB RAM, SSD 512GB, Intel i7', 4890.00, 'SKU-NTB-001', 2, NOW(), NOW(), TRUE),
('Teclado Mecânico Redragon', 'Switch blue, RGB, ABNT2', 329.90, 'SKU-TCL-002', 3, NOW(), NOW(), TRUE),
('Mouse Gamer Logitech G403', 'Sensor Hero 25K, 6 botões programáveis', 279.00, 'SKU-MSE-003', 3, NOW(), NOW(), TRUE),
('iPhone 14 128GB', 'Apple A15 Bionic, câmera dupla 12MP', 5299.00, 'SKU-IPH-004', 8, NOW(), NOW(), TRUE),
('Air Fryer Mondial 4L', '1500W, timer 60min', 399.00, 'SKU-AIR-005', 7, NOW(), NOW(), TRUE),
('Camiseta Dry Fit Nike', 'Tecnologia Dri-FIT, preta', 89.90, 'SKU-CAM-006', 5, NOW(), NOW(), TRUE),
('Chuteira Society Adidas Predator', 'Controle total, couro natural', 459.90, 'SKU-CHU-007', 6, NOW(), NOW(), TRUE),
('Livro "O Poder do Hábito"', 'Charles Duhigg - Edição capa dura', 48.90, 'SKU-LVR-008', 4, NOW(), NOW(), TRUE),
('Headset Gamer HyperX Cloud II', '7.1 Surround, vermelho', 589.00, 'SKU-HDS-009', 9, NOW(), NOW(), TRUE),
('Processador AMD Ryzen 7 5800X', '8 núcleos, 16 threads, 105W', 1890.00, 'SKU-CPU-010', 2, NOW(), NOW(), TRUE),
('Smart TV 55" Samsung 4K', 'Crystal UHD, HDR, Alexa integrada', 2899.00, 'SKU-TV4-011', 1, NOW(), NOW(), TRUE),
('Cadeira Gamer ThunderX3 TGC12', 'Reclinável 180°, suporte lombar', 1199.00, 'SKU-CAD-012', 9, NOW(), NOW(), TRUE),
('Perfume Creed Aventus 100ml', 'Masculino, eau de parfum', 1890.00, 'SKU-PRF-013', 10, NOW(), NOW(), TRUE),
('Bicicleta Caloi Aro 29', '21 marchas, freio a disco', 1599.00, 'SKU-BIC-014', 6, NOW(), NOW(), TRUE),
('Panela de Pressão Elétrica 6L', 'Philips Walita, 15 funções', 679.00, 'SKU-PNL-015', 7, NOW(), NOW(), TRUE);


INSERT INTO tb_estoque (produto_id, quantidade, criado_em, atualizado_em)
VALUES
(1, 18, NOW(), NOW()),
(2, 65, NOW(), NOW()),
(3, 42, NOW(), NOW()),
(4, 12, NOW(), NOW()),
(5, 38, NOW(), NOW()),
(6, 120, NOW(), NOW()),
(7, 22, NOW(), NOW()),
(8, 85, NOW(), NOW()),
(9, 31, NOW(), NOW()),
(10, 19, NOW(), NOW()),
(11, 9, NOW(), NOW()),
(12, 27, NOW(), NOW()),
(13, 45, NOW(), NOW()),
(14, 16, NOW(), NOW()),
(15, 33, NOW(), NOW());
