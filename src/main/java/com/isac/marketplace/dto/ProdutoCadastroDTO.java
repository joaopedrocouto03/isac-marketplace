package com.isac.marketplace.dto;

import java.math.BigDecimal;

/**
 * Dados recebidos no cadastro de um produto.
 */
public class ProdutoCadastroDTO {

    private Long lojaId;
    private String nome;
    private String descricao;
    private BigDecimal preco;

    public ProdutoCadastroDTO() {
    }

    public ProdutoCadastroDTO(Long lojaId, String nome, String descricao, BigDecimal preco) {
        this.lojaId = lojaId;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public Long getLojaId() {
        return lojaId;
    }

    public void setLojaId(Long lojaId) {
        this.lojaId = lojaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
