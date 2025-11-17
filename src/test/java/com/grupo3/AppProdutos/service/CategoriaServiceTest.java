package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.AtualizarCategoriaRequest;
import com.grupo3.AppProdutos.dto.CriarCategoriaRequest;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.model.Produto;
import com.grupo3.AppProdutos.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Nested
    @DisplayName("Testes de Criação de Categoria")
    class CriarCategoria {

        @Test
        void criarCategoria_deveSalvarQuandoValido() {
            var dto = new CriarCategoriaRequest("Eletrônicos", null);

            var categoriaSalva = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônicos")).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

            var resultado = categoriaService.salvarCategoria(dto);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Eletrônicos", resultado.getNome());
            assertNull(resultado.getParent());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void criarCategoria_deveSalvarComParentQuandoValido() {
            var parentCategoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            var dto = new CriarCategoriaRequest("Smartphones", 1L);

            var categoriaSalva = Categoria.builder()
                    .id(2L)
                    .nome("Smartphones")
                    .parent(parentCategoria)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(parentCategoria));
            when(categoriaRepository.existsByNomeAndParent("Smartphones", parentCategoria)).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

            var resultado = categoriaService.salvarCategoria(dto);

            assertNotNull(resultado);
            assertEquals(2L, resultado.getId());
            assertEquals("Smartphones", resultado.getNome());
            assertEquals(1L, resultado.getParent().getId());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void criarCategoria_deveLancarQuandoNomeVazio() {
            var dto = new CriarCategoriaRequest("", null);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void criarCategoria_deveLancarQuandoNomeNull() {
            var dto = new CriarCategoriaRequest(null, null);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void criarCategoria_deveLancarQuandoJaExisteNoMesmoNivel() {
            var dto = new CriarCategoriaRequest("Eletrônicos", null);

            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônicos")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void criarCategoria_deveLancarQuandoJaExisteComMesmoParent() {
            var parentCategoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .build();

            var dto = new CriarCategoriaRequest("Smartphones", 1L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(parentCategoria));
            when(categoriaRepository.existsByNomeAndParent("Smartphones", parentCategoria)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void criarCategoria_deveLancarQuandoParentNaoExiste() {
            var dto = new CriarCategoriaRequest("Smartphones", 99L);

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(dto));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de listagem de Categorias")
    class ListarCategorias {

        @Test
        void listarCategorias_deveRetornarListaCompleta() {
            var c1 = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();

            var c2 = Categoria.builder()
                    .id(2L)
                    .nome("Alimentos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();

            when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));

            var lista = categoriaService.buscarListaDeCategorias();

            assertEquals(2, lista.size());
            assertTrue(lista.stream().anyMatch(it -> it.getId().equals(1L)));
            assertTrue(lista.stream().anyMatch(it -> it.getId().equals(2L)));
        }

        @Test
        void listarCategorias_deveRetornarListaVazia() {
            when(categoriaRepository.findAll()).thenReturn(List.of());

            var lista = categoriaService.buscarListaDeCategorias();

            assertEquals(0, lista.size());
        }

        @Test
        void buscarCategoriaPorId_deveRetornarQuandoEncontrada() {
            var categoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            var resultado = categoriaService.buscarCategoriaPorId(1L);

            assertEquals(1L, resultado.getId());
            assertEquals("Eletrônicos", resultado.getNome());
        }

        @Test
        void buscarCategoriaPorId_deveLancarQuandoNaoEncontrada() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.buscarCategoriaPorId(99L));
        }
    }

    @Nested
    @DisplayName("Testes de atualização de Categorias")
    class AtualizarCategoria {

        @Test
        void atualizarCategoria_deveAtualizarQuandoValido() {
            var categoriaExistente = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            var dto = new AtualizarCategoriaRequest("Eletrônicos e Tecnologia", null);

            var categoriaAtualizada = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos e Tecnologia")
                    .parent(null)
                    .criadoEm(categoriaExistente.getCriadoEm())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônicos e Tecnologia")).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

            var resultado = categoriaService.atualizarCategoria(1L, dto);

            assertEquals("Eletrônicos e Tecnologia", resultado.getNome());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void atualizarCategoria_deveAtualizarParentQuandoValido() {
            var novoParent = Categoria.builder()
                    .id(2L)
                    .nome("Tecnologia")
                    .parent(null)
                    .build();

            var categoriaExistente = Categoria.builder()
                    .id(1L)
                    .nome("Smartphones")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            var dto = new AtualizarCategoriaRequest("Smartphones", 2L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(novoParent));
            when(categoriaRepository.existsByNomeAndParent("Smartphones", novoParent)).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaExistente);

            var resultado = categoriaService.atualizarCategoria(1L, dto);

            assertNotNull(resultado);
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void atualizarCategoria_deveLancarQuandoNaoEncontrada() {
            var dto = new AtualizarCategoriaRequest("Eletrônicos Updated", null);

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(99L, dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoNomeVazio() {
            var dto = new AtualizarCategoriaRequest("", null);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoNomeDuplicado() {
            var categoriaExistente = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();

            var dto = new AtualizarCategoriaRequest("Alimentos", null);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.existsByNomeAndParentIsNull("Alimentos")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoHierarquiaCircular() {
            var categoriaFilha = Categoria.builder()
                    .id(2L)
                    .nome("Smartphones")
                    .parent(null)
                    .build();

            var categoriaPai = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(categoriaFilha)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();

            categoriaFilha.setParent(categoriaPai);

            var dto = new AtualizarCategoriaRequest("Eletrônicos", 2L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaPai));
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoriaFilha));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, dto));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoParentNaoExiste() {
            var categoriaExistente = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .parent(null)
                    .build();

            var dto = new AtualizarCategoriaRequest("Eletrônicos", 99L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, dto));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de exclusão de Categorias")
    class ExcluirCategoria {

        @Test
        void deletarCategoria_deveDeletarQuandoExiste() {
            var categoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .subcategorias(new ArrayList<>())
                    .produtos(new ArrayList<>())
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            categoriaService.deletarCategoria(1L);

            verify(categoriaRepository).delete(categoria);
        }

        @Test
        void deletarCategoria_deveLancarQuandoNaoExiste() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(99L));
            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        void deletarCategoria_deveLancarQuandoTemProdutosAssociados() {
            var produto = new Produto();
            produto.setId(1L);

            var categoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .subcategorias(new ArrayList<>())
                    .produtos(List.of(produto))
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(1L));
            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        void deletarCategoria_deveLancarQuandoTemSubcategorias() {
            var subcategoria = Categoria.builder()
                    .id(2L)
                    .nome("Smartphones")
                    .build();

            var categoria = Categoria.builder()
                    .id(1L)
                    .nome("Eletrônicos")
                    .subcategorias(List.of(subcategoria))
                    .produtos(new ArrayList<>())
                    .build();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(1L));
            verify(categoriaRepository, never()).delete(any());
        }
    }
}
