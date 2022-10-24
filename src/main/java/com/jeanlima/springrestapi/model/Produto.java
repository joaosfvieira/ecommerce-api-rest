package com.jeanlima.springrestapi.model;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") //nao obrigatório - vai ser igual o nome do atributo
    private Integer id;

    @Column
    private String descricao;
    
    @Column(precision = 10,scale = 2)
    private BigDecimal preco;

    @OneToOne(mappedBy = "produto")
    private Estoque estoque;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public BigDecimal getPreco() {
        return preco;
    }
    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    
    
}
