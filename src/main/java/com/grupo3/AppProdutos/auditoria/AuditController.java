package com.grupo3.AppProdutos.auditoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> buscar(@RequestParam(required = false) String entidade) {
        if (entidade == null)
            return auditLogRepository.findAll();

        return auditLogRepository.findByEntidade(entidade);
    }
}
