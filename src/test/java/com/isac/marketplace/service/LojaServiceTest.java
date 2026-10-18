package com.isac.marketplace.service;

import com.isac.marketplace.dto.LojaCadastroDTO;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.CnpjInvalidoException;
import com.isac.marketplace.exception.EmailInvalidoException;
import com.isac.marketplace.exception.LojaJaCadastradaException;
import com.isac.marketplace.exception.TelefoneInvalidoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LojaService - cadastro de lojas")
class LojaServiceTest {

    @Mock
    private LojaRepository lojaRepository;

    @InjectMocks
    private LojaService lojaService;

    private LojaCadastroDTO dtoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new LojaCadastroDTO(
                "Mercadinho da Enseada",
                "11.222.333/0001-81",
                "Alimentação",
                "(81) 99999-8888",
                "contato@mercadinhoenseada.com.br",
                "Rua das Palmeiras, 123 - Enseada dos Corais"
        );
    }

    @Test
    @DisplayName("Cadastra a loja com sucesso quando os dados são válidos")
    void deveCadastrarLojaComDadosValidos() {
        when(lojaRepository.existsByCnpj(anyString())).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> {
            Loja loja = invocacao.getArgument(0);
            loja.setId(1L);
            return loja;
        });

        Loja lojaCadastrada = lojaService.cadastrarLoja(dtoValido);

        assertNotNull(lojaCadastrada.getId());
        assertEquals("Mercadinho da Enseada", lojaCadastrada.getNomeFantasia());
        assertEquals(StatusAssinatura.PENDENTE, lojaCadastrada.getStatusAssinatura());
        verify(lojaRepository, times(1)).save(any(Loja.class));
    }

    @Test
    @DisplayName("Salva CNPJ e telefone somente com números")
    void deveSalvarCnpjETelefoneSomenteComNumeros() {
        when(lojaRepository.existsByCnpj(anyString())).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        lojaService.cadastrarLoja(dtoValido);

        ArgumentCaptor<Loja> captor = ArgumentCaptor.forClass(Loja.class);
        verify(lojaRepository).save(captor.capture());
        assertEquals("11222333000181", captor.getValue().getCnpj());
        assertEquals("81999998888", captor.getValue().getTelefoneWhatsapp());
    }

    @Test
    @DisplayName("Aceita telefone fixo com 10 dígitos")
    void deveAceitarTelefoneFixoComDezDigitos() {
        dtoValido.setTelefoneWhatsapp("(81) 3333-4444");
        when(lojaRepository.existsByCnpj(anyString())).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Loja loja = lojaService.cadastrarLoja(dtoValido);

        assertEquals("8133334444", loja.getTelefoneWhatsapp());
    }

    @Test
    @DisplayName("Rejeita nome fantasia em branco")
    void deveLancarExcecaoQuandoNomeFantasiaVazio() {
        dtoValido.setNomeFantasia("  ");

        assertThrows(CampoObrigatorioException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita CNPJ nulo")
    void deveLancarExcecaoQuandoCnpjNulo() {
        dtoValido.setCnpj(null);

        assertThrows(CampoObrigatorioException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita categoria em branco")
    void deveLancarExcecaoQuandoCategoriaVazia() {
        dtoValido.setCategoria("");

        assertThrows(CampoObrigatorioException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita CNPJ com menos de 14 dígitos")
    void deveLancarExcecaoQuandoCnpjComTamanhoInvalido() {
        dtoValido.setCnpj("123.456");

        assertThrows(CnpjInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita CNPJ com dígitos verificadores incorretos")
    void deveRejeitarCnpjComDigitosVerificadoresIncorretos() {
        dtoValido.setCnpj("11.222.333/0001-82");

        assertThrows(CnpjInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita e-mail sem arroba")
    void deveLancarExcecaoQuandoEmailInvalido() {
        dtoValido.setEmail("email-sem-arroba");

        assertThrows(EmailInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita telefone com poucos dígitos")
    void deveLancarExcecaoQuandoTelefoneInvalido() {
        dtoValido.setTelefoneWhatsapp("123");

        assertThrows(TelefoneInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    @DisplayName("Rejeita CNPJ já cadastrado")
    void deveLancarExcecaoQuandoCnpjJaCadastrado() {
        when(lojaRepository.existsByCnpj("11222333000181")).thenReturn(true);

        assertThrows(LojaJaCadastradaException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }
}
