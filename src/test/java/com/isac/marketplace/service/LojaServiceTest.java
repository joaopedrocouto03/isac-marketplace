package com.isac.marketplace.service;

import com.isac.marketplace.dto.LojaCadastroDTO;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.CnpjInvalidoException;
import com.isac.marketplace.exception.LojaJaCadastradaException;
import com.isac.marketplace.exception.TelefoneInvalidoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes da classe LojaService — funcionalidade de cadastro de lojas
 * parceiras do marketplace comunitário do ISAC.
 *
 * Esta classe serve como REFERÊNCIA para a estruturação dos demais testes
 * do projeto: os cinco primeiros cenários já estão implementados e
 * passando; os dois últimos representam o "esqueleto" de cenários que
 * ainda serão implementados nas próximas entregas (estão marcados com
 * @Disabled e um comentário TODO explicando o motivo).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LojaService - Cadastro de lojas parceiras")
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
                "12.345.678/0001-99",
                "Alimentação",
                "(81) 99999-8888",
                "contato@mercadinhoenseada.com.br",
                "Rua das Palmeiras, 123 - Enseada dos Corais, Cabo de Santo Agostinho/PE"
        );
    }

    // ---------------------------------------------------------------
    // Cenário 1: cadastro com sucesso (caminho feliz)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve cadastrar a loja com sucesso quando todos os dados são válidos")
    void deveCadastrarLojaComDadosValidos() {
        when(lojaRepository.existsByCnpj(anyString())).thenReturn(false);
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocacao -> {
            Loja loja = invocacao.getArgument(0);
            loja.setId(1L);
            return loja;
        });

        Loja lojaCadastrada = lojaService.cadastrarLoja(dtoValido);

        assertNotNull(lojaCadastrada);
        assertNotNull(lojaCadastrada.getId());
        assertEquals(dtoValido.getNomeFantasia(), lojaCadastrada.getNomeFantasia());
        assertEquals(StatusAssinatura.PENDENTE, lojaCadastrada.getStatusAssinatura());
        verify(lojaRepository, times(1)).save(any(Loja.class));
    }

    // ---------------------------------------------------------------
    // Cenário 2: campo obrigatório não preenchido
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve lançar exceção quando o nome fantasia não é informado")
    void deveLancarExcecaoQuandoNomeFantasiaVazio() {
        dtoValido.setNomeFantasia("  ");

        assertThrows(CampoObrigatorioException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    // ---------------------------------------------------------------
    // Cenário 3: dados inválidos — CNPJ fora do padrão
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve lançar exceção quando o CNPJ informado tem formato inválido")
    void deveLancarExcecaoQuandoCnpjInvalido() {
        dtoValido.setCnpj("123.456");

        assertThrows(CnpjInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    // ---------------------------------------------------------------
    // Cenário 4: dados inválidos — telefone/WhatsApp fora do padrão
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve lançar exceção quando o telefone/WhatsApp informado é inválido")
    void deveLancarExcecaoQuandoTelefoneInvalido() {
        dtoValido.setTelefoneWhatsapp("123");

        assertThrows(TelefoneInvalidoException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    // ---------------------------------------------------------------
    // Cenário 5: loja/CNPJ já cadastrado (duplicidade)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Deve lançar exceção quando já existe loja cadastrada com o mesmo CNPJ")
    void deveLancarExcecaoQuandoCnpjJaCadastrado() {
        when(lojaRepository.existsByCnpj(anyString())).thenReturn(true);

        assertThrows(LojaJaCadastradaException.class, () -> lojaService.cadastrarLoja(dtoValido));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    // =================================================================
    // Esqueleto de cenários futuros — ainda não implementados.
    // Mantidos aqui para deixar explícito o que o grupo já planejou
    // testar nas próximas entregas, conforme pedido no enunciado.
    // =================================================================

    @Test
    @Disabled("TODO: depende da integração com o gateway de pagamento (próxima sprint)")
    @DisplayName("Deve ativar a assinatura da loja após a confirmação do pagamento")
    void deveAtivarAssinaturaAposConfirmacaoDePagamento() {
        // Ideia do cenário: dado um pagamento confirmado pelo gateway,
        // o status da loja deve mudar de PENDENTE para ATIVA.
        fail("Teste ainda não implementado.");
    }

    @Test
    @Disabled("TODO: depende do serviço de busca do chatbot, ainda não implementado")
    @DisplayName("Não deve retornar lojas com assinatura inativa na busca do chatbot")
    void naoDeveRetornarLojasComAssinaturaInativaNaBuscaDoBot() {
        // Ideia do cenário: o serviço de busca do bot (a ser criado) deve
        // filtrar apenas lojas com StatusAssinatura.ATIVA ao responder
        // um cliente no WhatsApp.
        fail("Teste ainda não implementado.");
    }

    @Test
    @Disabled("TODO: definir regra de validação completa dos dígitos verificadores do CNPJ")
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores incorretos")
    void deveRejeitarCnpjComDigitosVerificadoresIncorretos() {
        // Atualmente validamos apenas a quantidade de dígitos (14).
        // Próximo passo: implementar o algoritmo oficial de validação
        // dos dígitos verificadores do CNPJ.
        fail("Teste ainda não implementado.");
    }
}
