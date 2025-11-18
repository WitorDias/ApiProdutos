package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.UsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioResponse;
import com.grupo3.AppProdutos.mapper.UsuarioMapper;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRequest usuarioRequest){

        validarUsuario(usuarioRequest);
        Usuario usuario = UsuarioMapper.toEntity(usuarioRequest);
        usuarioRepository.save(usuario);

        return UsuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse buscarUsuarioPorId(Long id){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado.")
        );
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new RuntimeException("usuario não encontrado")
        );
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void validarUsuario(UsuarioRequest usuarioRequest){
        if(usuarioRepository.existsByNome(usuarioRequest.nome())){
            throw new RuntimeException("Este usuário já está em uso");
        }
        if(usuarioRepository.existsByEmail(usuarioRequest.email())){
            throw new RuntimeException("Este email já está em uso");
        }
    }
}
