package com.grupo3.AppProdutos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest (
        @NotBlank(message = "O nome de usuario é obrigatório") String nome,
        @NotBlank(message = "A senha é obrigatória") String senha,
        @Email(message = "O email é obrigatório")
        @NotBlank(message = "O email é obrigatório") String email)
{
}
