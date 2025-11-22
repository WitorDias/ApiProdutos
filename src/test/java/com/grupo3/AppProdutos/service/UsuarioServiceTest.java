package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.auditoria.AuditService;
import com.grupo3.AppProdutos.auditoria.TipoOperacao;
import com.grupo3.AppProdutos.dto.UsuarioDTO.AtualizarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.CriarUsuarioRequest;
import com.grupo3.AppProdutos.dto.UsuarioDTO.UsuarioResponse;
import com.grupo3.AppProdutos.exception.EmailJaExisteException;
import com.grupo3.AppProdutos.exception.UsuarioNaoEncontradoException;
import com.grupo3.AppProdutos.model.Usuario;
import com.grupo3.AppProdutos.model.enums.Role;
import com.grupo3.AppProdutos.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@teste.com");
        usuario.setSenha("senha123");
        usuario.setAtivo(true);
        usuario.setRoles(new HashSet<>(Set.of(Role.CLIENTE)));
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
        return usuario;
    }

    @Nested
    @DisplayName("Criar Usuário")
    class CriarUsuario {

        @Test
        @DisplayName("Deve criar usuário com sucesso")
        void deveCriarUsuarioComSucesso() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "João Silva",
                    "senha123",
                    "joao@teste.com",
                    Set.of(Role.CLIENTE)
            );

            when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("senhaEncoded");
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            UsuarioResponse resultado = usuarioService.criarUsuario(request);

            assertNotNull(resultado);
            assertEquals("João Silva", resultado.nome());
            assertEquals("joao@teste.com", resultado.email());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
            verify(passwordEncoder, times(1)).encode("senha123");
            verify(auditService, times(1)).registrar(
                    eq("Usuario"),
                    eq(1L),
                    eq(TipoOperacao.CREATE),
                    isNull(),
                    any(Usuario.class)
            );
        }

        @Test
        @DisplayName("Deve codificar a senha ao criar usuário")
        void deveCodificarSenhaAoCriar() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "João Silva",
                    "senha123",
                    "joao@teste.com",
                    Set.of(Role.CLIENTE)
            );

            when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("senhaEncoded");
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });

            usuarioService.criarUsuario(request);

            verify(usuarioRepository, times(1)).save(argThat(usuario ->
                    usuario.getSenha().equals("senhaEncoded")
            ));
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void deveLancarExcecaoQuandoEmailJaExiste() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "João Silva",
                    "senha123",
                    "joao@teste.com",
                    Set.of(Role.CLIENTE)
            );

            when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(true);

            assertThrows(EmailJaExisteException.class, () ->
                usuarioService.criarUsuario(request)
            );

            verify(usuarioRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("Listar Usuários")
    class ListarUsuarios {

        @Test
        @DisplayName("Deve listar apenas usuários ativos")
        void deveListarApenasUsuariosAtivos() {
            Usuario usuario1 = criarUsuario();
            usuario1.setAtivo(true);

            Usuario usuario2 = criarUsuario();
            usuario2.setId(2L);
            usuario2.setEmail("maria@teste.com");
            usuario2.setAtivo(true);

            Usuario usuario3 = criarUsuario();
            usuario3.setId(3L);
            usuario3.setEmail("inativo@teste.com");
            usuario3.setAtivo(false);

            when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2, usuario3));

            List<UsuarioResponse> resultado = usuarioService.listarUsuarios();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().noneMatch(u -> u.email().equals("inativo@teste.com")));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários ativos")
        void deveRetornarListaVaziaQuandoNaoHaUsuariosAtivos() {
            Usuario usuario = criarUsuario();
            usuario.setAtivo(false);

            when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

            List<UsuarioResponse> resultado = usuarioService.listarUsuarios();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há usuários")
        void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
            when(usuarioRepository.findAll()).thenReturn(List.of());

            List<UsuarioResponse> resultado = usuarioService.listarUsuarios();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Buscar Usuário por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar usuário por ID com sucesso")
        void deveBuscarUsuarioPorIdComSucesso() {
            Usuario usuario = criarUsuario();
            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));

            UsuarioResponse resultado = usuarioService.buscarUsuarioPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            assertEquals("João Silva", resultado.nome());
            assertEquals("joao@teste.com", resultado.email());
            verify(usuarioRepository, times(1)).findByIdAndAtivoTrue(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                usuarioService.buscarUsuarioPorId(999L)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário está inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                usuarioService.buscarUsuarioPorId(1L)
            );
        }
    }

    @Nested
    @DisplayName("Atualizar Usuário")
    class AtualizarUsuario {

        @Test
        @DisplayName("Deve atualizar nome do usuário")
        void deveAtualizarNomeDoUsuario() {
            Usuario usuario = criarUsuario();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("João Santos", null);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            UsuarioResponse resultado = usuarioService.atualizarUsuario(1L, request);

            assertNotNull(resultado);
            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getNome().equals("João Santos")
            ));
            verify(auditService, times(1)).registrar(
                    eq("Usuario"),
                    eq(1L),
                    eq(TipoOperacao.UPDATE),
                    any(Usuario.class),
                    any(Usuario.class)
            );
        }

        @Test
        @DisplayName("Deve atualizar senha do usuário")
        void deveAtualizarSenhaDoUsuario() {
            Usuario usuario = criarUsuario();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, "novaSenha123");

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncoded");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            UsuarioResponse resultado = usuarioService.atualizarUsuario(1L, request);

            assertNotNull(resultado);
            verify(passwordEncoder, times(1)).encode("novaSenha123");
            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getSenha().equals("novaSenhaEncoded")
            ));
        }

        @Test
        @DisplayName("Deve atualizar nome e senha simultaneamente")
        void deveAtualizarNomeESenha() {
            Usuario usuario = criarUsuario();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("João Santos", "novaSenha123");

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncoded");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            UsuarioResponse resultado = usuarioService.atualizarUsuario(1L, request);

            assertNotNull(resultado);
            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getNome().equals("João Santos") && u.getSenha().equals("novaSenhaEncoded")
            ));
        }

        @Test
        @DisplayName("Não deve alterar quando nome é vazio")
        void naoDeveAlterarQuandoNomeVazio() {
            Usuario usuario = criarUsuario();
            String nomeOriginal = usuario.getNome();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("", null);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.atualizarUsuario(1L, request);

            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getNome().equals(nomeOriginal)
            ));
        }

        @Test
        @DisplayName("Não deve alterar quando nome é apenas espaços")
        void naoDeveAlterarQuandoNomeApenasEspacos() {
            Usuario usuario = criarUsuario();
            String nomeOriginal = usuario.getNome();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("   ", null);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.atualizarUsuario(1L, request);

            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getNome().equals(nomeOriginal)
            ));
        }

        @Test
        @DisplayName("Não deve alterar quando senha é vazia")
        void naoDeveAlterarQuandoSenhaVazia() {
            Usuario usuario = criarUsuario();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, "");

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.atualizarUsuario(1L, request);

            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("Não deve alterar nada quando ambos são nulos")
        void naoDeveAlterarNadaQuandoAmbosNulos() {
            Usuario usuario = criarUsuario();
            String nomeOriginal = usuario.getNome();
            String senhaOriginal = usuario.getSenha();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, null);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.atualizarUsuario(1L, request);

            verify(usuarioRepository, times(1)).save(argThat(u ->
                    u.getNome().equals(nomeOriginal) && u.getSenha().equals(senhaOriginal)
            ));
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Novo Nome", null);
            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                usuarioService.atualizarUsuario(999L, request)
            );

            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Deletar Usuário")
    class DeletarUsuario {

        @Test
        @DisplayName("Deve desativar usuário com sucesso (soft delete)")
        void deveDesativarUsuarioComSucesso() {
            Usuario usuario = criarUsuario();
            usuario.setAtivo(true);

            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.deletarUsuario(1L);

            verify(usuarioRepository, times(1)).save(argThat(u -> u.getAtivo() == false));
            verify(auditService, times(1)).registrar(
                    eq("Usuario"),
                    eq(1L),
                    eq(TipoOperacao.DELETE),
                    any(Usuario.class),
                    any(Usuario.class)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                usuarioService.deletarUsuario(999L)
            );

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Não deve deletar usuário já inativo")
        void naoDeveDeletarUsuarioJaInativo() {
            when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoEncontradoException.class, () ->
                usuarioService.deletarUsuario(1L)
            );
        }
    }

    @Nested
    @DisplayName("Validar Usuário")
    class ValidarUsuario {

        @Test
        @DisplayName("Deve validar usuário com sucesso quando email não existe")
        void deveValidarUsuarioComSucessoQuandoEmailNaoExiste() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "João Silva",
                    "senha123",
                    "joao@teste.com",
                    Set.of(Role.CLIENTE)
            );

            when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(false);

            assertDoesNotThrow(() ->
                usuarioService.validarUsuario(request)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void deveLancarExcecaoQuandoEmailJaExisteNaValidacao() {
            CriarUsuarioRequest request = new CriarUsuarioRequest(
                    "João Silva",
                    "senha123",
                    "joao@teste.com",
                    Set.of(Role.CLIENTE)
            );

            when(usuarioRepository.existsByEmail("joao@teste.com")).thenReturn(true);

            assertThrows(EmailJaExisteException.class, () ->
                usuarioService.validarUsuario(request)
            );
        }
    }
}


