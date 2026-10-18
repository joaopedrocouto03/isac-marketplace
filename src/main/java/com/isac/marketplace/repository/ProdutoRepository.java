package com.isac.marketplace.repository;

import com.isac.marketplace.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByLojaId(Long lojaId);

    // usado na busca do chatbot: so produtos marcados como disponiveis
    List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);
}
