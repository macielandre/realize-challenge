# Teste Java Realize
### Utilizando a api

1 . Instalar java 25 localmente
2 . Iniciar seu docker local
3 . Rodar o comando

`docker compose up`

4 . Acessar  o swagger 

`http://localhost:8072/swagger-ui/index.html#/`

5 . Utilizar a rota POST /api/public/account para criar uma conta
6 . Utilizar o id da conta retornado na request anterior para gerar um token na request GET /api/public/token
7 . Utilizar as rotas enviando esse token gerado como bearer

### Decisoes técnicas

- Nao permitir que uma transferencia impacte em outras sendo executadas.
- Uuid nos ids para garantir baixa colisao e permissao para utilizacao de diversos padroes de desenvolvimento sem maiores problemas (separacao de databases, data tiering, ETL's, etc).
- Uuid's no banco como binarios para garantir melhor performance e indexacao.
- Tratamento de erros globais para facilitar entendimento de erros.
- Chave de idempotencia para nao permitir transacoes repetidas.
- Separacao de pastas utilizando um MVC básico para manter o projeto simples.
- Mysql por ser um banco de dados prático e muito resiliente em cenários de alta concorrencia.
- Utilizacao de token jwt para utilizar dados inseridos no próprio token para garantir seguranca e nao permitir açoes inesperadas em contas indevidas.
