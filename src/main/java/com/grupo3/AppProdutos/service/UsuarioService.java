package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.UsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioResponse;
import com.grupo3.AppProdutos.exception.EmailJaExisteException;
import com.grupo3.AppProdutos.exception.NomeUsuarioJaExisteException;
import com.grupo3.AppProdutos.exception.UsuarioNaoEncontradoException;
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
                () -> new UsuarioNaoEncontradoException(id)
        );
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new UsuarioNaoEncontradoException(id)
        );
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void validarUsuario(UsuarioRequest usuarioRequest){
        if(usuarioRepository.existsByNome(usuarioRequest.nome())){
            throw new NomeUsuarioJaExisteException(usuarioRequest.nome());
        }
        if(usuarioRepository.existsByEmail(usuarioRequest.email())){
            throw new EmailJaExisteException(usuarioRequest.email());
        }
    }
}
