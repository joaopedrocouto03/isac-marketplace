package com.isac.marketplace.model;

/**
 * Status da assinatura paga de uma loja no marketplace.
 *
 * PENDENTE  -> loja cadastrada, aguardando confirmação de pagamento.
 * ATIVA     -> assinatura em dia; a loja aparece nas buscas do chatbot.
 * INATIVA   -> assinatura vencida/cancelada; a loja não aparece nas buscas.
 */
public enum StatusAssinatura {
    PENDENTE,
    ATIVA,
    INATIVA
}
