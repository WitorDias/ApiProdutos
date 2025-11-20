package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.UsuarioDTO.AtualizarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.CriarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.UsuarioResponse;
import com.grupo3.AppProdutos.exception.EmailJaExisteException;
import com.grupo3.AppProdutos.exception.UsuarioNaoEncontradoException;
import com.grupo3.AppProdutos.mapper.UsuarioMapper;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse criarUsuario(CriarUsuarioRequest criarUsuarioRequest){

        validarUsuario(criarUsuarioRequest);
        Usuario usuario = UsuarioMapper.toEntity(criarUsuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        return UsuarioMapper.toResponse(usuario);
    }

    public List<UsuarioResponse> listarUsuarios(){
        return usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse buscarUsuarioPorId(Long id){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new UsuarioNaoEncontradoException(id)
        );
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarUsuario(Long id, AtualizarUsuarioRequest request){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new UsuarioNaoEncontradoException(id)
        );

        if(request.nome() != null && !request.nome().trim().isEmpty()){
            usuario.setNome(request.nome());
        }

        if(request.senha() != null && !request.senha().isEmpty()){
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        usuarioRepository.save(usuario);
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

    public void validarUsuario(CriarUsuarioRequest criarUsuarioRequest){
        if(usuarioRepository.existsByEmail(criarUsuarioRequest.email())){
            throw new EmailJaExisteException(criarUsuarioRequest.email());
        }
    }
}
