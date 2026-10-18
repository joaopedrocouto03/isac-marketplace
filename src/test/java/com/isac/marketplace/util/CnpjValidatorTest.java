package com.isac.marketplace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CnpjValidator")
class CnpjValidatorTest {

    @Test
    @DisplayName("Aceita CNPJ válido com máscara")
    void deveAceitarCnpjValidoComMascara() {
        assertTrue(CnpjValidator.isValido("11.222.333/0001-81"));
    }

    @Test
    @DisplayName("Aceita CNPJ válido somente com números")
    void deveAceitarCnpjValidoSemMascara() {
        assertTrue(CnpjValidator.isValido("11222333000181"));
    }

    @Test
    @DisplayName("Aceita CNPJ cujo primeiro dígito verificador é zero")
    void deveAceitarCnpjComPrimeiroDigitoVerificadorZero() {
        assertTrue(CnpjValidator.isValido("11.222.333/0005-05"));
    }

    @Test
    @DisplayName("Aceita CNPJ cujo segundo dígito verificador é zero")
    void deveAceitarCnpjComSegundoDigitoVerificadorZero() {
        assertTrue(CnpjValidator.isValido("11.222.333/0018-20"));
    }

    @Test
    @DisplayName("Rejeita CNPJ com dígito verificador errado")
    void deveRejeitarCnpjComDigitoVerificadorErrado() {
        assertFalse(CnpjValidator.isValido("11.222.333/0001-82"));
    }

    @Test
    @DisplayName("Rejeita CNPJ com todos os dígitos iguais")
    void deveRejeitarCnpjComTodosOsDigitosIguais() {
        assertFalse(CnpjValidator.isValido("11.111.111/1111-11"));
    }

    @Test
    @DisplayName("Rejeita CNPJ com menos de 14 dígitos")
    void deveRejeitarCnpjCurto() {
        assertFalse(CnpjValidator.isValido("123.456"));
    }

    @Test
    @DisplayName("Rejeita CNPJ nulo ou vazio")
    void deveRejeitarCnpjNuloOuVazio() {
        assertFalse(CnpjValidator.isValido(null));
        assertFalse(CnpjValidator.isValido(""));
    }

    @Test
    @DisplayName("Rejeita texto sem números")
    void deveRejeitarTextoSemNumeros() {
        assertFalse(CnpjValidator.isValido("abc.def.ghi/jklm-no"));
    }
}
