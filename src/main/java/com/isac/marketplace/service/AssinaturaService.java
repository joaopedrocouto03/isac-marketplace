package com.isac.marketplace.service;

import com.isac.marketplace.exception.LojaNaoEncontradaException;
import com.isac.marketplace.exception.PagamentoNaoConfirmadoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Controle da assinatura paga das lojas (ativar, cancelar e vencer).
 */
@Service
public class AssinaturaService {

    static final int DIAS_DE_ASSINATURA = 30;

    private final LojaRepository lojaRepository;
    private final Clock clock;

    public AssinaturaService(LojaRepository lojaRepository, Clock clock) {
        this.lojaRepository = lojaRepository;
        this.clock = clock;
    }

    public Loja ativarAssinatura(Long lojaId, boolean pagamentoConfirmado) {
        if (!pagamentoConfirmado) {
            throw new PagamentoNaoConfirmadoException(
                    "A assinatura só pode ser ativada após a confirmação do pagamento.");
        }

        Loja loja = buscarLoja(lojaId);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);
        loja.setDataVencimentoAssinatura(LocalDate.now(clock).plusDays(DIAS_DE_ASSINATURA));
        return lojaRepository.save(loja);
    }

    public Loja cancelarAssinatura(Long lojaId) {
        Loja loja = buscarLoja(lojaId);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);
        return lojaRepository.save(loja);
    }

    // inativa as lojas ativas cujo vencimento ja passou; retorna quantas foram inativadas
    public int inativarAssinaturasVencidas() {
        LocalDate hoje = LocalDate.now(clock);
        List<Loja> ativas = lojaRepository.findByStatusAssinatura(StatusAssinatura.ATIVA);

        int inativadas = 0;
        for (Loja loja : ativas) {
            LocalDate vencimento = loja.getDataVencimentoAssinatura();
            if (vencimento != null && vencimento.isBefore(hoje)) {
                loja.setStatusAssinatura(StatusAssinatura.INATIVA);
                lojaRepository.save(loja);
                inativadas++;
            }
        }
        return inativadas;
    }

    private Loja buscarLoja(Long lojaId) {
        return lojaRepository.findById(lojaId)
                .orElseThrow(() -> new LojaNaoEncontradaException("Loja não encontrada."));
    }
}
