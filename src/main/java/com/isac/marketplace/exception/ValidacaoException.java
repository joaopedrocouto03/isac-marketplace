package com.isac.marketplace.exception;

/**
 * Exceção base para erros de validação de negócio no cadastro de lojas.
 * As exceções mais específicas (CNPJ inválido, campo obrigatório, etc.)
 * herdam desta classe.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String message) {
        super(message);
    }
}
