CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entidade VARCHAR(100) NOT NULL,
    entidade_id VARCHAR(100) NOT NULL,
    operacao VARCHAR(20) NOT NULL,
    dado_anterior TEXT NULL,
    dado_posterior TEXT NULL,
    usuario VARCHAR(150) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_entidade ON audit_log(entidade);
CREATE INDEX idx_audit_log_entidade_id ON audit_log(entidade_id);
CREATE INDEX idx_audit_log_operacao ON audit_log(operacao);
CREATE INDEX idx_audit_log_usuario ON audit_log(usuario);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);

