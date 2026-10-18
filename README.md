# ISAC Marketplace

Marketplace comunitário do ISAC (Instituto Social Ambiental do Cabo de Santo Agostinho),
para substituir o grupo de WhatsApp usado pela comunidade de Enseada dos Corais.

Ideia: lojas locais se cadastram e pagam uma assinatura para anunciar. Quando um cliente
pergunta por um produto no chatbot do WhatsApp, o bot responde com as lojas (de assinatura
ativa) que têm o produto.

Este repositório tem a parte de regras de negócio em Java (Spring Boot) com testes unitários
(JUnit 5 + Mockito). Ainda não há controllers, telas nem integração com o WhatsApp.

## Classes e testes

| Classe | O que faz | Testes |
|---|---|---|
| `CnpjValidator` | valida CNPJ (tamanho e dígitos verificadores) | `CnpjValidatorTest` |
| `LojaService` | cadastro de lojas (campos obrigatórios, CNPJ, e-mail, telefone, duplicidade) | `LojaServiceTest` |
| `ProdutoService` | cadastro de produtos (só loja com assinatura ativa), disponibilidade, listagem | `ProdutoServiceTest` |
| `BuscaProdutoService` | busca do chatbot: lojas ativas que têm o produto e texto da resposta | `BuscaProdutoServiceTest` |
| `AssinaturaService` | ativar, cancelar e vencer a assinatura das lojas | `AssinaturaServiceTest` |

Os repositórios (`LojaRepository`, `ProdutoRepository`) são mockados nos testes, então eles
rodam sem banco de dados.

## Estrutura

```
src/main/java/com/isac/marketplace/
  model/        Loja, Produto, StatusAssinatura
  dto/          LojaCadastroDTO, ProdutoCadastroDTO
  repository/   LojaRepository, ProdutoRepository
  service/      LojaService, ProdutoService, BuscaProdutoService, AssinaturaService
  util/         CnpjValidator
  config/       ClockConfig
  exception/    exceções de validação

src/test/java/com/isac/marketplace/
  service/      LojaServiceTest, ProdutoServiceTest, BuscaProdutoServiceTest, AssinaturaServiceTest
  util/         CnpjValidatorTest
```

## Como rodar os testes

```bash
mvn test
```

Resultado esperado: 52 testes, 0 falhas, 0 ignorados.

## Próximos passos

- Controllers (API REST) para cadastro de lojas e produtos
- Integração do chatbot com `BuscaProdutoService`
- Integração com pagamento para chamar `AssinaturaService.ativarAssinatura`
- Agendar `inativarAssinaturasVencidas` para rodar todo dia
