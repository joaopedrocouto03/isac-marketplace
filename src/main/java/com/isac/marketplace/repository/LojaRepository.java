package com.isac.marketplace.repository;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LojaRepository extends JpaRepository<Loja, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<Loja> findByCnpj(String cnpj);

    /**
     * Usado futuramente pelo serviço de busca do chatbot: retorna apenas
     * lojas com assinatura ativa para uma determinada categoria/produto.
     */
    List<Loja> findByStatusAssinaturaAndCategoriaIgnoreCase(StatusAssinatura status, String categoria);
}
