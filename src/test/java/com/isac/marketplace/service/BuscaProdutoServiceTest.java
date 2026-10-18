package com.isac.marketplace.service;

import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Produto;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscaProdutoService - busca do chatbot")
class BuscaProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    private BuscaProdutoService buscaService;

    @BeforeEach
    void setUp() {
        buscaService = new BuscaProdutoService(produtoRepository);
    }

    private Loja criarLoja(String nome, String cnpj, StatusAssinatura status) {
        Loja loja = new Loja(nome, cnpj, "Moda", "81999998888", "loja@email.com", "Rua A, 1");
        loja.setStatusAssinatura(status);
        return loja;
    }

    private Produto criarProduto(String nome, Loja loja) {
        return new Produto(nome, "", new BigDecimal("50.00"), loja);
    }

    @Test
    @DisplayName("Retorna a loja ativa que tem o produto")
    void deveRetornarLojaAtivaQueTemOProduto() {
        Loja loja = criarLoja("Loja do Zé", "11111111000191", StatusAssinatura.ATIVA);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis"))
                .thenReturn(Arrays.asList(criarProduto("Tênis azul", loja)));

        List<Loja> resultado = buscaService.buscarLojasPorProduto("tênis");

        assertEquals(1, resultado.size());
        assertEquals("Loja do Zé", resultado.get(0).getNomeFantasia());
    }

    @Test
    @DisplayName("Não retorna loja com assinatura inativa")
    void naoDeveRetornarLojasComAssinaturaInativa() {
        Loja inativa = criarLoja("Loja Antiga", "22222222000191", StatusAssinatura.INATIVA);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis"))
                .thenReturn(Arrays.asList(criarProduto("Tênis azul", inativa)));

        assertTrue(buscaService.buscarLojasPorProduto("tênis").isEmpty());
    }

    @Test
    @DisplayName("Não retorna loja com assinatura pendente")
    void naoDeveRetornarLojasComAssinaturaPendente() {
        Loja pendente = criarLoja("Loja Nova", "33333333000191", StatusAssinatura.PENDENTE);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis"))
                .thenReturn(Arrays.asList(criarProduto("Tênis azul", pendente)));

        assertTrue(buscaService.buscarLojasPorProduto("tênis").isEmpty());
    }

    @Test
    @DisplayName("Entre várias lojas, retorna só as ativas")
    void deveRetornarApenasAsLojasAtivas() {
        Loja ativaA = criarLoja("Loja A", "44444444000191", StatusAssinatura.ATIVA);
        Loja inativa = criarLoja("Loja B", "55555555000191", StatusAssinatura.INATIVA);
        Loja ativaC = criarLoja("Loja C", "66666666000191", StatusAssinatura.ATIVA);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("camisa"))
                .thenReturn(Arrays.asList(
                        criarProduto("Camisa polo", ativaA),
                        criarProduto("Camisa social", inativa),
                        criarProduto("Camisa estampada", ativaC)));

        List<Loja> resultado = buscaService.buscarLojasPorProduto("camisa");

        assertEquals(2, resultado.size());
        assertEquals("Loja A", resultado.get(0).getNomeFantasia());
        assertEquals("Loja C", resultado.get(1).getNomeFantasia());
    }

    @Test
    @DisplayName("Não repete a loja que tem vários produtos parecidos")
    void naoDeveRepetirALojaComVariosProdutos() {
        Loja loja = criarLoja("Loja A", "44444444000191", StatusAssinatura.ATIVA);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("camisa"))
                .thenReturn(Arrays.asList(
                        criarProduto("Camisa polo", loja),
                        criarProduto("Camisa social", loja)));

        assertEquals(1, buscaService.buscarLojasPorProduto("camisa").size());
    }

    @Test
    @DisplayName("Retorna lista vazia quando nenhum produto é encontrado")
    void deveRetornarListaVaziaQuandoNaoEncontrarProduto() {
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("violino"))
                .thenReturn(Collections.<Produto>emptyList());

        assertTrue(buscaService.buscarLojasPorProduto("violino").isEmpty());
    }

    @Test
    @DisplayName("Rejeita termo de busca vazio")
    void deveLancarExcecaoQuandoTermoVazio() {
        assertThrows(CampoObrigatorioException.class, () -> buscaService.buscarLojasPorProduto("   "));
        verifyNoInteractions(produtoRepository);
    }

    @Test
    @DisplayName("Rejeita termo de busca nulo")
    void deveLancarExcecaoQuandoTermoNulo() {
        assertThrows(CampoObrigatorioException.class, () -> buscaService.buscarLojasPorProduto(null));
        verifyNoInteractions(produtoRepository);
    }

    @Test
    @DisplayName("Rejeita termo de busca com 1 caractere")
    void deveLancarExcecaoQuandoTermoMuitoCurto() {
        assertThrows(CampoObrigatorioException.class, () -> buscaService.buscarLojasPorProduto("a"));
        verifyNoInteractions(produtoRepository);
    }

    @Test
    @DisplayName("Remove espaços do início e do fim antes de buscar")
    void deveRemoverEspacosDoTermoAntesDeBuscar() {
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis"))
                .thenReturn(Collections.<Produto>emptyList());

        buscaService.buscarLojasPorProduto("  tênis  ");

        verify(produtoRepository).findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis");
    }

    @Test
    @DisplayName("Resposta do bot lista nome e WhatsApp da loja")
    void deveMontarRespostaDoBotComAsLojas() {
        Loja loja = criarLoja("Loja do Zé", "11111111000191", StatusAssinatura.ATIVA);
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("tênis"))
                .thenReturn(Arrays.asList(criarProduto("Tênis azul", loja)));

        String resposta = buscaService.montarRespostaBot("tênis");

        assertTrue(resposta.contains("Encontrei 1 loja(s)"));
        assertTrue(resposta.contains("Loja do Zé"));
        assertTrue(resposta.contains("81999998888"));
    }

    @Test
    @DisplayName("Resposta do bot avisa quando não há lojas")
    void deveMontarRespostaDoBotQuandoNaoHaLojas() {
        when(produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("violino"))
                .thenReturn(Collections.<Produto>emptyList());

        String resposta = buscaService.montarRespostaBot("violino");

        assertTrue(resposta.contains("Não encontrei nenhuma loja"));
    }
}
