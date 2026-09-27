package com.isac.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação do Marketplace Comunitário do ISAC.
 *
 * O projeto tem como objetivo substituir o grupo de WhatsApp usado pela
 * comunidade de Enseada dos Corais (Cabo de Santo Agostinho) para divulgar
 * produtos e serviços, criando um marketplace onde lojas parceiras pagam
 * uma assinatura para anunciar, e um chatbot no WhatsApp responde aos
 * clientes indicando quais lojas cadastradas possuem o produto buscado.
 */
@SpringBootApplication
public class MarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
