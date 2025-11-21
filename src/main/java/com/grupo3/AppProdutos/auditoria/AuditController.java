package com.grupo3.AppProdutos.auditoria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
@Tag(name = "Auditoria", description = "Operações de consulta dos logs de auditoria")
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    @Operation(
            summary = "Buscar logs de auditoria",
            description = "Retorna a lista de logs de auditoria. "
                    + "Caso um parâmetro 'entidade' seja informado, retorna apenas os logs filtrados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de logs retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuditLog.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso negado – apenas ADMIN pode acessar este endpoint",
                            content = @Content
                    )
            }
    )
    public List<AuditLog> buscar(
            @Parameter(description = "Filtra os logs pela entidade (opcional)", example = "Usuario")
            @RequestParam(required = false) String entidade
    ) {
        if (entidade == null)
            return auditLogRepository.findAll();

        return auditLogRepository.findByEntidade(entidade);
    }
}
