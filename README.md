# Governanca Cooperativa API

## Sobre o Projeto
API hibrida (REST + Server-Driven UI) para gerenciamento de assembleias em cooperativas.

O sistema opera em duas frentes:
1. **Core de negocio**: endpoints REST para pautas, sessoes, votos e apuracao.
2. **Mobile Presentation**: endpoints de UI Server-Driven que entregam telas em JSON para o aplicativo.

## Tecnologias

| Tecnologia | Versao/Finalidade |
| :--- | :--- |
| **Java** | 21 (LTS) - Linguagem principal |
| **Spring Boot** | 3.5.10 - Framework da aplicacao |
| **PostgreSQL** | 16 - Banco de dados relacional |
| **Flyway** | 11.x - Versionamento e migracoes de schema |
| **OpenFeign** | Spring Cloud - Cliente HTTP declarativo |
| **Caelum Stella** | 2.1.6 - Validacao local de CPF |
| **SpringDoc** | 2.7.0 - OpenAPI e Swagger UI |
| **Docker** | Banco de dados local via container |

## Quick Start

Execute os comandos abaixo na raiz do projeto:

```bash
# 1. Subir o PostgreSQL
docker compose up -d

# 2. Executar a aplicacao
./mvnw clean spring-boot:run
```

Se estiver no Windows e preferir sem wrapper:

```bash
mvn clean spring-boot:run
```

## Modelo de Votação & Anonimato

O projeto adota práticas de **Security by Design** para garantir a privacidade e o sigilo do voto, separando a verificação de elegibilidade do depósito físico da cédula no banco de dados.

### Estruturas Principais de Persistência
- **`registro_participacao`**: Registra que um associado exerceu o seu direito de votar em uma pauta específica. Contém o `associadoId` (CPF), a `pautaId`, um hash único do token de participação (`token_hash`) e um identificador público chamado **`protocolo_publico`** (UUID). Não há nenhuma ligação estrutural com a escolha do voto.
- **`urna_voto`**: Contém apenas a pauta vinculada, a escolha registrada (`SIM` / `NAO`) e o timestamp. Não possui colunas como `associado_id`, `cpf` ou qualquer ponte com a identidade do eleitor.

---

### Decisões de Arquitetura e Mitigação de Riscos

#### 1. Prevenção de Correlação Temporal (Timing Attack)
Em transações JDBC síncronas, se a gravação da participação e da urna ocorresse no mesmo instante em milissegundos, um atacante com acesso ao banco de dados (ou logs de transação/WAL) poderia inferir quem votou em quê por ordenação simples.
- **Solução aplicada**: Os timestamps de consumo do token (`data_consumo` em `registro_participacao`) e de gravação do voto (`data_deposito` em `urna_voto`) são truncados para minutos (`.truncatedTo(ChronoUnit.MINUTES)`). Isso cria um agrupamento homogêneo de registros por janela de tempo, destruindo o alinhamento temporal 1:1 e mitigando correlações simples.

#### 2. Protocolo Público vs. ID de Cédula
Para fornecer um comprovante de recebimento ao eleitor sem vazar a chave do voto, a API retorna o campo `protocolo` na resposta de sucesso. Esse protocolo corresponde ao UUID aleatório público (`protocolo_publico`) gerado na tabela `registro_participacao`. A chave primária da tabela `urna_voto` nunca é exposta na resposta ou em logs da aplicação.

#### 3. Limitações de Design e Escopo (Anonimato Persistido vs. Absoluto)
*Esta é uma versão demonstrativa (portfólio) que prioriza a simplicidade de uso das APIs e telas móveis existentes, possuindo limitações conhecidas:*
- **Acoplamento em Camada de Transporte (Payload HTTP)**: O endpoint POST `/v1/pautas/{id}/votos` recebe a identidade (CPF) e a escolha (`votoEscolha`) no mesmo payload HTTP. Embora os dados sejam desacoplados imediatamente no banco de dados, em nível de rede (proxies, logs de servidores HTTP/WAF) há uma junção temporária dessas informações. Em um cenário real altamente regulado, o fluxo seria dividido em dois endpoints separados (emissão de token de votação cega e posterior depósito na urna contendo apenas o token).
- **Limitação de Baixo Volume**: O truncamento temporal para minutos é eficaz em cenários de tráfego simultâneo. Contudo, em sessões com baixíssimo volume (ex: um único voto em uma hora), o timestamp arredondado ainda pode permitir inferências indiretas se correlacionado com eventos externos (como o associado assinando fisicamente a ata de presença).

---

### Solução de Problemas Locais (Flyway Checksum Mismatch)
> [!IMPORTANT]
> Caso você já tenha executado uma versão anterior do projeto localmente e encontre erros de **checksum mismatch** no Flyway ao iniciar a aplicação (devido a alterações nos scripts de migração), execute o comando abaixo para remover os volumes locais do Docker e reinicie o ambiente:
> ```bash
> docker compose down -v
> docker compose up -d
> ```

## Contrato Atual da API

### Registro de voto

`POST /v1/pautas/{pautaId}/votos`

Corpo da requisicao:

```json
{
  "associadoId": "87868790067",
  "votoEscolha": "SIM"
}
```

Resposta de sucesso:

```json
{
  "protocolo": "uuid",
  "pautaId": "uuid",
  "status": "REGISTRADO"
}
```

O endpoint nao devolve CPF mascarado nem a escolha registrada.

### Consulta de resultado

`GET /v1/pautas/{pautaId}/resultado`

Durante a sessao:

```json
{
  "pautaId": "uuid",
  "totalSim": null,
  "totalNao": null,
  "status": "EM_ANDAMENTO"
}
```

Apos o fechamento da sessao, os totais finais sao divulgados normalmente.

## Como Testar

O projeto inclui um fluxo manual em [api_testes.http](c:/Users/guilh/Desktop/1%20projetos%20e%20repositorios%20git/governanca-cooperativa-api/api_testes.http).

Esse arquivo cobre:
1. criacao de pauta
2. abertura de sessao
3. tela mobile de voto
4. registro de votos
5. consulta de resultado
6. cenarios de erro, como CPF invalido e sessao expirada

Ferramenta recomendada:
- VS Code com a extensao REST Client

## Testes Automatizados

Para rodar a suite completa com banco:

```bash
docker compose up -d
mvn test
```

## Estrategia de Resiliencia

A validacao de aptidao do associado consulta uma API externa primaria.

Em caso de falha externa, o sistema usa fallback local com Caelum Stella para nao interromper a votacao.

## Notas de Configuracao

O arquivo `application.yaml` esta versionado para facilitar execucao local e demonstracao do projeto.

Em ambiente produtivo, o adequado seria mover credenciais para variaveis de ambiente ou um gerenciador de segredos.

## Endpoints

Swagger UI local:

`http://localhost:8080/docs`
