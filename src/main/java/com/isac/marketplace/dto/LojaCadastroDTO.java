package com.isac.marketplace.dto;

/**
 * Dados recebidos do formulário/API de cadastro de uma nova loja parceira.
 * Mantido como uma classe simples (sem validações via anotações) para que
 * toda a regra de negócio fique concentrada e testável em LojaService.
 */
public class LojaCadastroDTO {

    private String nomeFantasia;
    private String cnpj;
    private String categoria;
    private String telefoneWhatsapp;
    private String email;
    private String endereco;

    public LojaCadastroDTO() {
    }

    public LojaCadastroDTO(String nomeFantasia, String cnpj, String categoria,
                            String telefoneWhatsapp, String email, String endereco) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.categoria = categoria;
        this.telefoneWhatsapp = telefoneWhatsapp;
        this.email = email;
        this.endereco = endereco;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTelefoneWhatsapp() {
        return telefoneWhatsapp;
    }

    public void setTelefoneWhatsapp(String telefoneWhatsapp) {
        this.telefoneWhatsapp = telefoneWhatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
