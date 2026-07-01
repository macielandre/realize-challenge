# Teste Java Realize
### Utilizando a api

- Instalar java 25 localmente
- Iniciar seu docker local
- Rodar o comando

`docker compose up`

- Acessar  o swagger 

`http://localhost:8072/swagger-ui/index.html#/`

- Utilizar a rota POST /api/public/account para criar uma conta
- Utilizar o id da conta retornado na request anterior para gerar um token na request GET /api/public/token
- Utilizar as rotas enviando esse token gerado como bearer

### Decisoes técnicas

- Nao permitir que uma transferencia impacte em outras sendo executadas.
- Uuid nos ids para garantir baixa colisao e permissao para utilizacao de diversos padroes de desenvolvimento sem maiores problemas (separacao de databases, data tiering, ETL's, etc).
- Uuid's no banco como binarios para garantir melhor performance e indexacao.
- Tratamento de erros globais para facilitar entendimento de erros.
- Chave de idempotencia para nao permitir transacoes repetidas.
- Separacao de pastas utilizando um MVC básico para manter o projeto simples.
- Mysql por ser um banco de dados prático e muito resiliente em cenários de alta concorrencia.
- Utilizacao de token jwt para utilizar dados inseridos no próprio token para garantir seguranca e nao permitir açoes inesperadas em contas indevidas.
- Utilizacao de Integer ao invés de BigDecimal pois o Integer garante mais performance e facilita integracao com diversas api's bancarias.
