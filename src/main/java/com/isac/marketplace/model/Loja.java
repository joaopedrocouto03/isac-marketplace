package com.isac.marketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Loja parceira do marketplace. Paga uma assinatura para aparecer
 * nas respostas do chatbot.
 */
@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private String categoria;

    @Column(name = "telefone_whatsapp", nullable = false)
    private String telefoneWhatsapp;

    @Column(nullable = false)
    private String email;

    @Column
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_assinatura", nullable = false)
    private StatusAssinatura statusAssinatura;

    @Column(name = "data_vencimento_assinatura")
    private LocalDate dataVencimentoAssinatura;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    public Loja() {
        // exigido pelo JPA
    }

    public Loja(String nomeFantasia, String cnpj, String categoria,
                String telefoneWhatsapp, String email, String endereco) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.categoria = categoria;
        this.telefoneWhatsapp = telefoneWhatsapp;
        this.email = email;
        this.endereco = endereco;
        this.statusAssinatura = StatusAssinatura.PENDENTE;
        this.dataCadastro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusAssinatura getStatusAssinatura() {
        return statusAssinatura;
    }

    public void setStatusAssinatura(StatusAssinatura statusAssinatura) {
        this.statusAssinatura = statusAssinatura;
    }

    public LocalDate getDataVencimentoAssinatura() {
        return dataVencimentoAssinatura;
    }

    public void setDataVencimentoAssinatura(LocalDate dataVencimentoAssinatura) {
        this.dataVencimentoAssinatura = dataVencimentoAssinatura;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // so lojas com assinatura ativa aparecem para o chatbot
    public boolean isElegivelParaBuscaDoBot() {
        return this.statusAssinatura == StatusAssinatura.ATIVA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loja)) return false;
        Loja loja = (Loja) o;
        return Objects.equals(cnpj, loja.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnpj);
    }

    @Override
    public String toString() {
        return "Loja{id=" + id + ", nomeFantasia='" + nomeFantasia + "', cnpj='" + cnpj
                + "', statusAssinatura=" + statusAssinatura + "}";
    }
}
