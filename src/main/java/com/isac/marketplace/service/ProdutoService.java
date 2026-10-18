package com.isac.marketplace.service;

import com.isac.marketplace.dto.ProdutoCadastroDTO;
import com.isac.marketplace.exception.AssinaturaInativaException;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.LojaNaoEncontradaException;
import com.isac.marketplace.exception.PrecoInvalidoException;
import com.isac.marketplace.exception.ProdutoNaoEncontradoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Produto;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cadastro e manutencao dos produtos de cada loja.
 * Apenas lojas com assinatura ativa podem cadastrar produtos.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final LojaRepository lojaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, LojaRepository lojaRepository) {
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
    }

    public Produto cadastrarProduto(ProdutoCadastroDTO dto) {
        if (dto.getLojaId() == null) {
            throw new CampoObrigatorioException("A loja do produto é obrigatória.");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new CampoObrigatorioException("O nome do produto é obrigatório.");
        }
        if (dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrecoInvalidoException("O preço do produto deve ser maior que zero.");
        }

        Loja loja = buscarLoja(dto.getLojaId());
        if (!loja.isElegivelParaBuscaDoBot()) {
            throw new AssinaturaInativaException(
                    "A loja precisa estar com a assinatura ativa para cadastrar produtos.");
        }

        Produto produto = new Produto(dto.getNome().trim(), dto.getDescricao(), dto.getPreco(), loja);
        return produtoRepository.save(produto);
    }

    public Produto alterarDisponibilidade(Long produtoId, boolean disponivel) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado."));
        produto.setDisponivel(disponivel);
        return produtoRepository.save(produto);
    }

    public List<Produto> listarPorLoja(Long lojaId) {
        if (!lojaRepository.existsById(lojaId)) {
            throw new LojaNaoEncontradaException("Loja não encontrada.");
        }
        return produtoRepository.findByLojaId(lojaId);
    }

    private Loja buscarLoja(Long lojaId) {
        return lojaRepository.findById(lojaId)
                .orElseThrow(() -> new LojaNaoEncontradaException("Loja não encontrada."));
    }
}
