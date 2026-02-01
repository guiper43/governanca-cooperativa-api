# Governança Cooperativa API

## Sobre o Projeto
API REST desenvolvida para gerenciamento de assembleias em cooperativas. O sistema controla o ciclo de vida completo das votações: cadastro de pautas, abertura de sessões com tempo delimitado, recebimento de votos de associados e contabilização dos resultados.

## Tecnologias

| Tecnologia | Versão/Finalidade |
| :--- | :--- |
| **Java** | 21 (LTS) - Linguagem core. |
| **Spring Boot** | 3.5.10 - Framework de aplicação. |
| **PostgreSQL** | 16 - Banco de dados relacional. |
| **Flyway** | 11.x - Versionamento e automação de schemas de banco. |
| **OpenFeign** | Spring Cloud - Cliente HTTP declarativo para integração externa. |
| **Caelum Stella** | 2.1.6 - Validação de CPF (Motor de Fallback). |
| **SpringDoc** | 2.7.0 - Documentação OpenAPI automatizada. |
| **Docker** | Containerização do ambiente de banco de dados. |

## Quick Start

Execute os comandos abaixo na raiz do projeto para configurar o banco de dados e iniciar a aplicação:

```bash
# 1. Iniciar infraestrutura de banco de dados (PostgreSQL)
docker-compose up -d

# 2. Compilar e executar a aplicação (Spring Boot)
./mvnw clean spring-boot:run
```

## Estratégia de Resiliência
O sistema prioriza a continuidade do negócio acima da dependência externa. A validação de aptidão do associado (CPF) consulta uma API externa primária.

Devido à instabilidade inerente do serviço externo, foi implementado um mecanismo de **Fallback Local**:
1. O sistema tenta validar o CPF via API externa.
2. Em caso de falha (indisponibilidade ou timeout), a validação assume automaticamente um algoritmo local (Caelum Stella).
Isso garante que as assembleias e votações não sejam interrompidas por falhas em serviços de terceiros.

## Notas de Configuração e Segurança
O arquivo `application.properties` (ou `application.yaml`) contendo as credenciais de banco de dados foi intencionalmente versionado neste repositório.

Esta decisão visa facilitar a **avaliação técnica imediata** ("Clone and Run"), eliminando a necessidade de configuração de ambiente por parte do avaliador. Em um ambiente produtivo real, estas credenciais seriam injetadas via variáveis de ambiente ou gerenciadores de segredos (como AWS Secrets Manager), jamais expostas no controle de versão.

## Endpoints
A documentação interativa e especificação da API estão disponíveis no Swagger UI:

**URL Local:** [http://localhost:8080/docs](http://localhost:8080/docs)
