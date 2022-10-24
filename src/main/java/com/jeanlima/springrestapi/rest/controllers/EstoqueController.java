package com.jeanlima.springrestapi.rest.controllers;

import com.jeanlima.springrestapi.model.Estoque;
import com.jeanlima.springrestapi.model.Produto;
import com.jeanlima.springrestapi.repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;


/*
*  {
    "quantidade": 1,
    "produto": {
        "id": 1,
        "descricao": "Boné",
        "preco": 69.69
    }
}
*
* */

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {
    @Autowired
    EstoqueRepository estoqueRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public Estoque save(@RequestBody Estoque estoque ){
        return estoqueRepository.save(estoque);
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void update( @PathVariable Integer id, @RequestBody Estoque estoque ){
        estoqueRepository
                .findById(id)
                .map( p -> {
                    estoque.setId(p.getId());
                    estoqueRepository.save(estoque);
                    return estoque;
                }).orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Estoque não encontrado."));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id){
        estoqueRepository
                .findById(id)
                .map( p -> {
                    estoqueRepository.delete(p);
                    return Void.TYPE;
                }).orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Estoque não encontrado."));
    }

    @GetMapping("{id}")
    public Estoque getById(@PathVariable Integer id){
        return estoqueRepository
                .findById(id)
                .orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Estoque não encontrado."));
    }

    @GetMapping
    public Estoque find(@RequestParam String descricao ){
        return estoqueRepository.findByProduto_Descricao(descricao);
    }

    @PatchMapping("{id}")
    public void patch(@PathVariable Integer id, @RequestBody Estoque estoque) {
        estoqueRepository
                .findById(id)
                .map( estoqueExistente -> {
                    if(estoque.getQuantidade() != 0)
                        estoqueExistente.setQuantidade(estoque.getQuantidade());
                    estoqueRepository.save(estoqueExistente);
                    return estoqueExistente;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estoque não encontrado") );
    }
}
