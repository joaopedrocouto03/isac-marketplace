package com.isac.marketplace.service;

import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Produto;
import com.isac.marketplace.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Busca usada pelo chatbot do WhatsApp: dado o que o cliente digitou,
 * descobre quais lojas tem o produto.
 */
@Service
public class BuscaProdutoService {

    private static final int TAMANHO_MINIMO_BUSCA = 2;

    private final ProdutoRepository produtoRepository;

    public BuscaProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // retorna so lojas com assinatura ativa, sem repetir a mesma loja
    public List<Loja> buscarLojasPorProduto(String termo) {
        if (termo == null || termo.trim().length() < TAMANHO_MINIMO_BUSCA) {
            throw new CampoObrigatorioException(
                    "Informe pelo menos " + TAMANHO_MINIMO_BUSCA + " caracteres para a busca.");
        }

        List<Produto> produtos =
                produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(termo.trim());

        Set<Loja> lojas = new LinkedHashSet<>();
        for (Produto produto : produtos) {
            if (produto.getLoja().isElegivelParaBuscaDoBot()) {
                lojas.add(produto.getLoja());
            }
        }
        return new ArrayList<>(lojas);
    }

    // texto que o bot manda de volta para o cliente
    public String montarRespostaBot(String termo) {
        List<Loja> lojas = buscarLojasPorProduto(termo);
        String busca = termo.trim();

        if (lojas.isEmpty()) {
            return "Não encontrei nenhuma loja com \"" + busca + "\" no momento.";
        }

        StringBuilder resposta = new StringBuilder();
        resposta.append("Encontrei ").append(lojas.size())
                .append(" loja(s) com \"").append(busca).append("\":\n");
        for (Loja loja : lojas) {
            resposta.append("- ").append(loja.getNomeFantasia())
                    .append(" (WhatsApp: ").append(loja.getTelefoneWhatsapp()).append(")\n");
        }
        return resposta.toString().trim();
    }
}
