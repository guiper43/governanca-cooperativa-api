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

## Modelo de Votacao

O projeto foi ajustado para separar participacao do associado da cedula depositada.

Estruturas principais:
- `registro_participacao`: controla quem ja exerceu o direito de votar em uma pauta
- `urna_voto`: guarda apenas a cedula anonima

Objetivo:
- impedir associacao direta entre eleitor e escolha dentro da urna
- evitar exposicao de dados sensiveis no retorno do voto
- impedir divulgacao de parciais durante a sessao

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
