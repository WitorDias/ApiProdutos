package com.grupo3.AppProdutos.dto.UsuarioDTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        String nome,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,255}$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula e um dígito")
        @Size(min = 6, max = 255,
                message = "A senha deve ter entre 6 e 255 caracteres")
        String senha
) {
}

