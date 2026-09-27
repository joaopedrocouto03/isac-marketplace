# ISAC Marketplace

Marketplace comunitário do **ISAC — Instituto Social Ambiental do Cabo de
Santo Agostinho**, criado para substituir o grupo de WhatsApp usado pela
comunidade de Enseada dos Corais para divulgar produtos e serviços.

**Ideia geral do projeto:** lojas locais se cadastram e pagam uma
assinatura para anunciar no marketplace. Quando um cliente manda uma
mensagem no chatbot do WhatsApp perguntando por um produto, o bot responde
indicando quais lojas cadastradas (com assinatura ativa) oferecem aquele
produto.

## Escopo desta entrega

Conforme orientado pelo professor, esta entrega **não exige o projeto
completo nem os testes 100% funcionais**. O objetivo foi:

1. Escolher **uma classe do projeto** e implementá-la por completo.
2. Criar o **esqueleto dos testes** relacionados a ela, mostrando os
   cenários que o grupo pretende validar.

A classe escolhida foi **`LojaService`**, responsável pelo **cadastro de
lojas parceiras** — a funcionalidade equivalente a um "cadastro/registro
de usuário" no contexto deste projeto, e uma das bases para todo o resto
do sistema (sem loja cadastrada, não há o que o bot responder).

## Estrutura do projeto

```
src/main/java/com/isac/marketplace/
├── MarketplaceApplication.java     # classe principal Spring Boot
├── model/
│   ├── Loja.java                   # entidade JPA da loja parceira
│   └── StatusAssinatura.java       # enum: PENDENTE, ATIVA, INATIVA
├── dto/
│   └── LojaCadastroDTO.java        # dados recebidos no cadastro
├── repository/
│   └── LojaRepository.java         # acesso a dados (Spring Data JPA)
├── service/
│   └── LojaService.java            # ★ classe implementada nesta entrega
└── exception/                      # exceções de validação de negócio
    ├── ValidacaoException.java
    ├── CampoObrigatorioException.java
    ├── CnpjInvalidoException.java
    ├── TelefoneInvalidoException.java
    └── LojaJaCadastradaException.java

src/test/java/com/isac/marketplace/service/
└── LojaServiceTest.java            # ★ testes de LojaService
```

## Cenários de teste cobertos (`LojaServiceTest`)

Já implementados e passando:

1. ✅ Cadastro de loja com dados válidos (caminho feliz)
2. ✅ Campo obrigatório não preenchido (nome fantasia vazio)
3. ✅ CNPJ com formato inválido
4. ✅ Telefone/WhatsApp com formato inválido
5. ✅ CNPJ já cadastrado (loja duplicada)

Planejados para as próximas entregas (marcados com `@Disabled` e `TODO`
no código, mostrando o esqueleto do que já foi pensado):

6. ⏳ Ativação da assinatura após confirmação de pagamento
7. ⏳ Filtro de lojas com assinatura inativa na busca do chatbot
8. ⏳ Validação completa dos dígitos verificadores do CNPJ

## Como rodar os testes

Com o Maven instalado:

```bash
mvn test
```

Os testes usam **JUnit 5** e **Mockito** (ambos incluídos via
`spring-boot-starter-test`), mockando `LojaRepository` para testar a
regra de negócio de `LojaService` de forma isolada, sem depender de banco
de dados.

## Próximos passos

- Implementar `LojaController` (API REST) para expor o cadastro.
- Implementar o serviço de busca usado pelo chatbot do WhatsApp.
- Integrar com gateway de pagamento para ativação automática da
  assinatura.
- Implementar a entidade/serviço de **Produto**, associada à loja.
