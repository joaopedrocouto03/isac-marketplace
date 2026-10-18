package com.isac.marketplace.service;

import com.isac.marketplace.exception.LojaNaoEncontradaException;
import com.isac.marketplace.exception.PagamentoNaoConfirmadoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssinaturaService - assinatura das lojas")
class AssinaturaServiceTest {

    // data fixa para o teste nao depender do dia em que roda
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 18);

    @Mock
    private LojaRepository lojaRepository;

    private AssinaturaService assinaturaService;

    @BeforeEach
    void setUp() {
        Clock relogioFixo = Clock.fixed(Instant.parse("2026-10-18T12:00:00Z"), ZoneId.of("UTC"));
        assinaturaService = new AssinaturaService(lojaRepository, relogioFixo);
    }

    private Loja criarLoja(String cnpj, StatusAssinatura status) {
        Loja loja = new Loja("Loja " + cnpj, cnpj, "Moda", "81999998888", "loja@email.com", "Rua A, 1");
        loja.setId(1L);
        loja.setStatusAssinatura(status);
        return loja;
    }

    @Test
    @DisplayName("Ativa a assinatura quando o pagamento foi confirmado")
    void deveAtivarAssinaturaAposPagamentoConfirmado() {
        Loja loja = criarLoja("11222333000181", StatusAssinatura.PENDENTE);
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = assinaturaService.ativarAssinatura(1L, true);

        assertEquals(StatusAssinatura.ATIVA, resultado.getStatusAssinatura());
        assertEquals(HOJE.plusDays(30), resultado.getDataVencimentoAssinatura());
    }

    @Test
    @DisplayName("Reativa uma loja que estava inativa")
    void deveReativarLojaInativa() {
        Loja loja = criarLoja("11222333000181", StatusAssinatura.INATIVA);
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = assinaturaService.ativarAssinatura(1L, true);

        assertEquals(StatusAssinatura.ATIVA, resultado.getStatusAssinatura());
    }

    @Test
    @DisplayName("Não ativa a assinatura sem confirmação de pagamento")
    void naoDeveAtivarAssinaturaSemPagamento() {
        assertThrows(PagamentoNaoConfirmadoException.class, () -> assinaturaService.ativarAssinatura(1L, false));
        verifyNoInteractions(lojaRepository);
    }

    @Test
    @DisplayName("Rejeita ativar assinatura de loja inexistente")
    void deveLancarExcecaoAoAtivarLojaInexistente() {
        when(lojaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LojaNaoEncontradaException.class, () -> assinaturaService.ativarAssinatura(99L, true));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Cancela a assinatura de uma loja ativa")
    void deveCancelarAssinatura() {
        Loja loja = criarLoja("11222333000181", StatusAssinatura.ATIVA);
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja resultado = assinaturaService.cancelarAssinatura(1L);

        assertEquals(StatusAssinatura.INATIVA, resultado.getStatusAssinatura());
    }

    @Test
    @DisplayName("Rejeita cancelar assinatura de loja inexistente")
    void deveLancarExcecaoAoCancelarLojaInexistente() {
        when(lojaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LojaNaoEncontradaException.class, () -> assinaturaService.cancelarAssinatura(99L));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Inativa somente as lojas com assinatura vencida")
    void deveInativarSomenteAssinaturasVencidas() {
        Loja vencida = criarLoja("11111111000191", StatusAssinatura.ATIVA);
        vencida.setDataVencimentoAssinatura(HOJE.minusDays(1));
        Loja vigente = criarLoja("22222222000191", StatusAssinatura.ATIVA);
        vigente.setDataVencimentoAssinatura(HOJE.plusDays(5));
        Loja venceHoje = criarLoja("33333333000191", StatusAssinatura.ATIVA);
        venceHoje.setDataVencimentoAssinatura(HOJE);
        Loja semData = criarLoja("44444444000191", StatusAssinatura.ATIVA);

        when(lojaRepository.findByStatusAssinatura(StatusAssinatura.ATIVA))
                .thenReturn(Arrays.asList(vencida, vigente, venceHoje, semData));

        int inativadas = assinaturaService.inativarAssinaturasVencidas();

        assertEquals(1, inativadas);
        assertEquals(StatusAssinatura.INATIVA, vencida.getStatusAssinatura());
        assertEquals(StatusAssinatura.ATIVA, vigente.getStatusAssinatura());
        assertEquals(StatusAssinatura.ATIVA, venceHoje.getStatusAssinatura());
        assertEquals(StatusAssinatura.ATIVA, semData.getStatusAssinatura());
        verify(lojaRepository, times(1)).save(any(Loja.class));
    }

    @Test
    @DisplayName("Não faz nada quando não há lojas ativas")
    void naoDeveInativarNadaQuandoNaoHaLojasAtivas() {
        when(lojaRepository.findByStatusAssinatura(StatusAssinatura.ATIVA))
                .thenReturn(Collections.<Loja>emptyList());

        int inativadas = assinaturaService.inativarAssinaturasVencidas();

        assertEquals(0, inativadas);
        verify(lojaRepository, never()).save(any(Loja.class));
    }
}
