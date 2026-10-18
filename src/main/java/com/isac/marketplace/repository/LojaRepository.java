package com.isac.marketplace.repository;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LojaRepository extends JpaRepository<Loja, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<Loja> findByCnpj(String cnpj);

    List<Loja> findByStatusAssinatura(StatusAssinatura status);
}
