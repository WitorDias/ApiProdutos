package com.grupo3.AppProdutos.auditoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository repository;

    @Autowired
    private ObjectMapper mapper;

    public void registrar(
            String entidade,
            Object id,
            TipoOperacao operacao,
            Object before,
            Object after
    ) {
        try {
            AuditLog log = new AuditLog();
            FieldUtils.writeField(log, "entidade", entidade, true);
            FieldUtils.writeField(log, "entidadeId", String.valueOf(id), true);
            FieldUtils.writeField(log, "operacao", operacao, true);
            FieldUtils.writeField(log, "dadoAnterior", before != null ? mapper.writeValueAsString(before) : null, true);
            FieldUtils.writeField(log, "dadoPosterior", after != null ? mapper.writeValueAsString(after) : null, true);

            String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
            FieldUtils.writeField(log, "usuario", usuario, true);

            FieldUtils.writeField(log, "timestamp", LocalDateTime.now(), true);

            repository.save(log);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar auditoria", e);
        }
    }
}
