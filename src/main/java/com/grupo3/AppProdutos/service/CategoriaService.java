package com.grupo3.AppProdutos.service;

import com.grupo3.AppProdutos.dto.AtualizarCategoriaRequest;
import com.grupo3.AppProdutos.dto.CriarCategoriaRequest;
import com.grupo3.AppProdutos.exception.*;
import com.grupo3.AppProdutos.model.Categoria;
import com.grupo3.AppProdutos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> buscarListaDeCategorias(){
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria salvarCategoria(CriarCategoriaRequest request){
        validarNome(request.nome());

        Categoria parent = null;
        if(request.parentId() != null){
            parent = buscarCategoriaPorId(request.parentId());
        }

        validarDuplicidade(request.nome(), parent);

        Categoria categoria = Categoria.builder()
                .nome(request.nome())
                .parent(parent)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return categoriaRepository.save(categoria);
    }

    public Categoria buscarCategoriaPorId(Long id){
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, AtualizarCategoriaRequest request){
        validarNome(request.nome());

        var categoriaParaAtualizar = buscarCategoriaPorId(id);

        Categoria novoParent = null;
        if(request.parentId() != null){
            novoParent = buscarCategoriaPorId(request.parentId());
            validarHierarquiaCircular(id, novoParent);
        }

        if(!categoriaParaAtualizar.getNome().equals(request.nome()) ||
           !isMesmoParent(categoriaParaAtualizar.getParent(), novoParent)){
            validarDuplicidade(request.nome(), novoParent);
        }

        categoriaParaAtualizar.setNome(request.nome());
        categoriaParaAtualizar.setParent(novoParent);
        categoriaParaAtualizar.setAtualizadoEm(LocalDateTime.now());

        return categoriaRepository.save(categoriaParaAtualizar);
    }

    @Transactional
    public void deletarCategoria(Long id){
        var categoria = buscarCategoriaPorId(id);

        if(!categoria.getProdutos().isEmpty()){
            throw new CategoriaComDependenciasException("Não é possível deletar categoria com produtos associados");
        }

        if(!categoria.getSubcategorias().isEmpty()){
            throw new CategoriaComDependenciasException("Não é possível deletar categoria com subcategorias");
        }

        categoriaRepository.delete(categoria);
    }

    private void validarNome(String nome){
        if(nome == null || nome.trim().isEmpty()){
            throw new ValidacaoException("Nome da categoria não pode ser vazio");
        }
    }

    private void validarDuplicidade(String nome, Categoria parent){
        boolean existe;
        if(parent == null){
            existe = categoriaRepository.existsByNomeAndParentIsNull(nome);
        } else {
            existe = categoriaRepository.existsByNomeAndParent(nome, parent);
        }

        if(existe){
            throw new CategoriaJaExisteException(nome);
        }
    }

    private void validarHierarquiaCircular(Long categoriaId, Categoria novoParent){
        Categoria atual = novoParent;
        while(atual != null){
            if(atual.getId().equals(categoriaId)){
                throw new HierarquiaCircularException();
            }
            atual = atual.getParent();
        }
    }

    private boolean isMesmoParent(Categoria parent1, Categoria parent2){
        if(parent1 == null && parent2 == null){
            return true;
        }
        if(parent1 == null || parent2 == null){
            return false;
        }
        return parent1.getId().equals(parent2.getId());
    }
}
