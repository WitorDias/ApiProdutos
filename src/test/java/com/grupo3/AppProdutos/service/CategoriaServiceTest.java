package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.CategoriaDTO.AtualizarCategoriaRequest;
import com.grupo3.AppProdutos.dto.CategoriaDTO.CriarCategoriaRequest;
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
        void salvarCategoria_deveSalvarQuandoValido() {
            var request = new CriarCategoriaRequest("Eletrônicos", null);

            var categoriaSalva = new Categoria();
            categoriaSalva.setId(1L);
            categoriaSalva.setNome("Eletrônicos");
            categoriaSalva.setParent(null);
            categoriaSalva.setCriadoEm(LocalDateTime.now());
            categoriaSalva.setAtualizadoEm(LocalDateTime.now());

            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônicos")).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

            var resultado = categoriaService.salvarCategoria(request);

            assertNotNull(resultado);
            assertEquals("Eletrônicos", resultado.getNome());
            assertNull(resultado.getParent());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void salvarCategoria_deveSalvarComParent() {
            var parent = new Categoria();
            parent.setId(1L);
            parent.setNome("Eletrônicos");

            var request = new CriarCategoriaRequest("Smartphones", 1L);

            var categoriaSalva = new Categoria();
            categoriaSalva.setId(2L);
            categoriaSalva.setNome("Smartphones");
            categoriaSalva.setParent(parent);
            categoriaSalva.setCriadoEm(LocalDateTime.now());
            categoriaSalva.setAtualizadoEm(LocalDateTime.now());

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(parent));
            when(categoriaRepository.existsByNomeAndParent("Smartphones", parent)).thenReturn(false);
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

            var resultado = categoriaService.salvarCategoria(request);

            assertNotNull(resultado);
            assertEquals("Smartphones", resultado.getNome());
            assertEquals(parent, resultado.getParent());
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        void salvarCategoria_deveLancarQuandoNomeVazio() {
            var request = new CriarCategoriaRequest("", null);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(request));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void salvarCategoria_deveLancarQuandoNomeNulo() {
            var request = new CriarCategoriaRequest(null, null);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(request));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void salvarCategoria_deveLancarQuandoDuplicado() {
            var request = new CriarCategoriaRequest("Eletrônicos", null);

            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônicos")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(request));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void salvarCategoria_deveLancarQuandoParentNaoExiste() {
            var request = new CriarCategoriaRequest("Smartphones", 999L);

            when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.salvarCategoria(request));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Listagem de Categorias")
    class ListarCategorias {

        @Test
        void buscarListaDeCategorias_deveRetornarLista() {
            var cat1 = new Categoria();
            cat1.setId(1L);
            cat1.setNome("Eletrônicos");

            var cat2 = new Categoria();
            cat2.setId(2L);
            cat2.setNome("Livros");

            when(categoriaRepository.findAll()).thenReturn(List.of(cat1, cat2));

            var resultado = categoriaService.buscarListaDeCategorias();

            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().anyMatch(c -> c.getNome().equals("Eletrônicos")));
            assertTrue(resultado.stream().anyMatch(c -> c.getNome().equals("Livros")));
        }

        @Test
        void buscarCategoriaPorId_deveRetornarQuandoEncontrado() {
            var categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNome("Eletrônicos");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            var resultado = categoriaService.buscarCategoriaPorId(1L);

            assertEquals(1L, resultado.getId());
            assertEquals("Eletrônicos", resultado.getNome());
        }

        @Test
        void buscarCategoriaPorId_deveLancarQuandoNaoEncontrado() {
            when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.buscarCategoriaPorId(999L));
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Categoria")
    class AtualizarCategoria {

        @Test
        void atualizarCategoria_deveAtualizarQuandoValido() {
            var categoriaExistente = new Categoria();
            categoriaExistente.setId(1L);
            categoriaExistente.setNome("Eletrônicos");
            categoriaExistente.setParent(null);

            var request = new AtualizarCategoriaRequest("Eletrônica", null);

            var categoriaAtualizada = new Categoria();
            categoriaAtualizada.setId(1L);
            categoriaAtualizada.setNome("Eletrônica");
            categoriaAtualizada.setParent(null);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.existsByNomeAndParentIsNull("Eletrônica")).thenReturn(false);
            when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaAtualizada);

            var resultado = categoriaService.atualizarCategoria(1L, request);

            assertEquals("Eletrônica", resultado.getNome());
            verify(categoriaRepository).save(categoriaExistente);
        }

        @Test
        void atualizarCategoria_deveAtualizarParent() {
            var parent = new Categoria();
            parent.setId(2L);
            parent.setNome("Eletrônicos");

            var categoriaExistente = new Categoria();
            categoriaExistente.setId(1L);
            categoriaExistente.setNome("Smartphones");
            categoriaExistente.setParent(null);

            var request = new AtualizarCategoriaRequest("Smartphones", 2L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(parent));
            when(categoriaRepository.existsByNomeAndParent("Smartphones", parent)).thenReturn(false);
            when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaExistente);

            var resultado = categoriaService.atualizarCategoria(1L, request);

            assertEquals(parent, resultado.getParent());
            verify(categoriaRepository).save(categoriaExistente);
        }

        @Test
        void atualizarCategoria_deveLancarQuandoHierarquiaCircular() {
            var categoriaExistente = new Categoria();
            categoriaExistente.setId(1L);
            categoriaExistente.setNome("Eletrônicos");

            var subcategoria = new Categoria();
            subcategoria.setId(2L);
            subcategoria.setNome("Smartphones");
            subcategoria.setParent(categoriaExistente);

            var request = new AtualizarCategoriaRequest("Eletrônicos", 2L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(subcategoria));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, request));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoNomeVazio() {
            var categoriaExistente = new Categoria();
            categoriaExistente.setId(1L);
            categoriaExistente.setNome("Eletrônicos");

            var request = new AtualizarCategoriaRequest("", null);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, request));
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void atualizarCategoria_deveLancarQuandoDuplicado() {
            var categoriaExistente = new Categoria();
            categoriaExistente.setId(1L);
            categoriaExistente.setNome("Eletrônicos");

            var request = new AtualizarCategoriaRequest("Livros", null);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
            when(categoriaRepository.existsByNomeAndParentIsNull("Livros")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizarCategoria(1L, request));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão de Categoria")
    class DeletarCategoria {

        @Test
        void deletarCategoria_deveDeletarQuandoSemDependencias() {
            var categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNome("Eletrônicos");
            categoria.setProdutos(new ArrayList<>());
            categoria.setSubcategorias(new ArrayList<>());

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            categoriaService.deletarCategoria(1L);

            verify(categoriaRepository).delete(categoria);
        }

        @Test
        void deletarCategoria_deveLancarQuandoTemProdutos() {
            var categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNome("Eletrônicos");
            categoria.setSubcategorias(new ArrayList<>());

            var produto = new Produto();
            produto.setId(1L);
            produto.setNome("Produto Teste");
            categoria.setProdutos(List.of(produto));

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(1L));
            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        void deletarCategoria_deveLancarQuandoTemSubcategorias() {
            var categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNome("Eletrônicos");
            categoria.setProdutos(new ArrayList<>());

            var subcategoria = new Categoria();
            subcategoria.setId(2L);
            subcategoria.setNome("Smartphones");
            categoria.setSubcategorias(List.of(subcategoria));

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(1L));
            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        void deletarCategoria_deveLancarQuandoNaoEncontrada() {
            when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> categoriaService.deletarCategoria(999L));
            verify(categoriaRepository, never()).delete(any());
        }
    }
}

