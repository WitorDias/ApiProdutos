package com.grupo3.AppProdutos.dto.UsuarioDTO;

import com.grupo3.AppProdutos.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CriarUsuarioRequest (
        @NotBlank(message = "O nome de usuario é obrigatório")
        String nome,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 255, message = "A senha deve ter entre 6 e 255 caracteres")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula e um dígito"
        )
        String senha,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        Set<Role> roles
)
{
}

