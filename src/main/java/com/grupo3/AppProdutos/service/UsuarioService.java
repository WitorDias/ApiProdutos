package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
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
    private final AuditService auditService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional
    public UsuarioResponse criarUsuario(CriarUsuarioRequest criarUsuarioRequest){

        validarUsuario(criarUsuarioRequest);
        Usuario usuario = UsuarioMapper.toEntity(criarUsuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        auditService.registrar("Usuario", usuario.getId(), TipoOperacao.CREATE, null, clonarUsuario(usuario));

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

        Usuario antes = clonarUsuario(usuario);

        if(request.nome() != null && !request.nome().trim().isEmpty()){
            usuario.setNome(request.nome());
        }

        if(request.senha() != null && !request.senha().isEmpty()){
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        auditService.registrar("Usuario", usuario.getId(), TipoOperacao.UPDATE, antes, clonarUsuario(usuario));

        usuarioRepository.save(usuario);
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void deletarUsuario(Long id){
        var usuario = usuarioRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new UsuarioNaoEncontradoException(id)
        );

        Usuario antes = clonarUsuario(usuario);

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        auditService.registrar("Usuario", usuario.getId(), TipoOperacao.DELETE, antes, clonarUsuario(usuario));
    }

    public void validarUsuario(CriarUsuarioRequest criarUsuarioRequest){
        if(usuarioRepository.existsByEmail(criarUsuarioRequest.email())){
            throw new EmailJaExisteException(criarUsuarioRequest.email());
        }
    }

    private Usuario clonarUsuario(Usuario usuario) {
        if (usuario == null) return null;

        Usuario clone = new Usuario();
        clone.setId(usuario.getId());
        clone.setNome(usuario.getNome());
        clone.setEmail(usuario.getEmail());
        return clone;
    }

}
