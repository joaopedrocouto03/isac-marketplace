package com.isac.marketplace.service;

import com.isac.marketplace.dto.ProdutoCadastroDTO;
import com.isac.marketplace.exception.AssinaturaInativaException;
import com.isac.marketplace.exception.CampoObrigatorioException;
import com.isac.marketplace.exception.LojaNaoEncontradaException;
import com.isac.marketplace.exception.PrecoInvalidoException;
import com.isac.marketplace.exception.ProdutoNaoEncontradoException;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Produto;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - produtos das lojas")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private LojaRepository lojaRepository;

    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoService = new ProdutoService(produtoRepository, lojaRepository);
    }

    private Loja criarLoja(StatusAssinatura status) {
        Loja loja = new Loja("Mercadinho da Enseada", "11222333000181", "Alimentação",
                "81999998888", "contato@mercadinho.com.br", "Rua das Palmeiras, 123");
        loja.setId(1L);
        loja.setStatusAssinatura(status);
        return loja;
    }

    private ProdutoCadastroDTO criarDtoValido() {
        return new ProdutoCadastroDTO(1L, "Tapioca de queijo", "Tapioca recheada", new BigDecimal("8.50"));
    }

    @Test
    @DisplayName("Cadastra produto quando a loja está com assinatura ativa")
    void deveCadastrarProdutoDeLojaAtiva() {
        Loja loja = criarLoja(StatusAssinatura.ATIVA);
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Produto produto = produtoService.cadastrarProduto(criarDtoValido());

        assertEquals("Tapioca de queijo", produto.getNome());
        assertEquals(0, new BigDecimal("8.50").compareTo(produto.getPreco()));
        assertTrue(produto.isDisponivel());
        assertSame(loja, produto.getLoja());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita produto sem loja")
    void deveLancarExcecaoQuandoLojaNaoInformada() {
        ProdutoCadastroDTO dto = criarDtoValido();
        dto.setLojaId(null);

        assertThrows(CampoObrigatorioException.class, () -> produtoService.cadastrarProduto(dto));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita produto sem nome")
    void deveLancarExcecaoQuandoNomeVazio() {
        ProdutoCadastroDTO dto = criarDtoValido();
        dto.setNome("   ");

        assertThrows(CampoObrigatorioException.class, () -> produtoService.cadastrarProduto(dto));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita preço igual a zero")
    void deveLancarExcecaoQuandoPrecoZero() {
        ProdutoCadastroDTO dto = criarDtoValido();
        dto.setPreco(BigDecimal.ZERO);

        assertThrows(PrecoInvalidoException.class, () -> produtoService.cadastrarProduto(dto));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita preço negativo")
    void deveLancarExcecaoQuandoPrecoNegativo() {
        ProdutoCadastroDTO dto = criarDtoValido();
        dto.setPreco(new BigDecimal("-5.00"));

        assertThrows(PrecoInvalidoException.class, () -> produtoService.cadastrarProduto(dto));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita produto de loja inexistente")
    void deveLancarExcecaoQuandoLojaNaoExiste() {
        when(lojaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LojaNaoEncontradaException.class, () -> produtoService.cadastrarProduto(criarDtoValido()));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita produto de loja com assinatura pendente")
    void deveLancarExcecaoQuandoAssinaturaPendente() {
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(criarLoja(StatusAssinatura.PENDENTE)));

        assertThrows(AssinaturaInativaException.class, () -> produtoService.cadastrarProduto(criarDtoValido()));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Rejeita produto de loja com assinatura inativa")
    void deveLancarExcecaoQuandoAssinaturaInativa() {
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(criarLoja(StatusAssinatura.INATIVA)));

        assertThrows(AssinaturaInativaException.class, () -> produtoService.cadastrarProduto(criarDtoValido()));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Marca um produto como indisponível")
    void deveAlterarDisponibilidadeDoProduto() {
        Produto produto = new Produto("Cuscuz", "Cuscuz com ovo", new BigDecimal("6.00"), criarLoja(StatusAssinatura.ATIVA));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Produto atualizado = produtoService.alterarDisponibilidade(10L, false);

        assertFalse(atualizado.isDisponivel());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Rejeita alterar disponibilidade de produto inexistente")
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.alterarDisponibilidade(99L, false));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Lista os produtos de uma loja")
    void deveListarProdutosDaLoja() {
        Loja loja = criarLoja(StatusAssinatura.ATIVA);
        List<Produto> produtos = Arrays.asList(
                new Produto("Cuscuz", "", new BigDecimal("6.00"), loja),
                new Produto("Tapioca", "", new BigDecimal("8.50"), loja));
        when(lojaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.findByLojaId(1L)).thenReturn(produtos);

        List<Produto> resultado = produtoService.listarPorLoja(1L);

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Rejeita listar produtos de loja inexistente")
    void deveLancarExcecaoAoListarProdutosDeLojaInexistente() {
        when(lojaRepository.existsById(99L)).thenReturn(false);

        assertThrows(LojaNaoEncontradaException.class, () -> produtoService.listarPorLoja(99L));
        verify(produtoRepository, never()).findByLojaId(99L);
    }
}
