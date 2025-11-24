CREATE TABLE IF NOT EXISTS tb_usuario_roles (
    usuario_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, role),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(usuario_id) ON DELETE CASCADE
);

INSERT INTO tb_usuario_roles (usuario_id, role)
SELECT usuario_id, 'CLIENTE'
FROM tb_usuario
WHERE usuario_id NOT IN (SELECT DISTINCT usuario_id FROM tb_usuario_roles);

