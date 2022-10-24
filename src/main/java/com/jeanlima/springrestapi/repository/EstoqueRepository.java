package com.jeanlima.springrestapi.repository;

import com.jeanlima.springrestapi.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    @Query
    public Optional<Estoque> findByProduto_Descricao(String descricao);
}
