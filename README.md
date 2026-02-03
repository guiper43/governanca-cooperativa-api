# Governança Cooperativa API

## Sobre o Projeto
API Híbrida (REST + Server-Driven UI) desenvolvida para gerenciamento de assembleias em cooperativas.

O sistema opera em duas frentes:
1.  **Core de Negócio**: Endpoints REST clássicos para controle do ciclo de vida das votações (Pautas, Sessões e Votos).
2.  **Mobile Presentation**: Endpoints de UI Server-Driven que entregam telas prontas em JSON para o aplicativo, desacoplando o front-end das regras de negócio.

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

> **Nota**: O projeto utiliza **Maven Wrapper** para garantir que todos os desenvolvedores e pipelines de CI/CD utilizem a mesma versão do Maven ("Universal"). Caso precise regenerar os scripts ou atualizar a versão, execute: `mvn -N wrapper:wrapper`.

## Como Testar
Para facilitar a validação manual dos fluxos de ponta a ponta (Mobile e REST), o projeto inclui um arquivo de testes HTTP executável na raiz.

**Arquivo:** `api_testes.http`

O arquivo roteiriza o fluxo completo de uso:
1.  **Mobile**: Renderização das telas de cadastro e votação (Server-Driven UI).
2.  **REST**: Criação de Pautas, Abertura de Sessão e Persistência.
3.  **Cenários**: Fluxo feliz, validação de CPF e erros (ex: sessão expirada).

**Ferramenta Recomendada:**
*   **VS Code**: Utilize a extensão **REST Client** para executar as requisições diretamente do editor.
*   Basta abrir o arquivo e clicar nos botões `Send Request` que aparecem acima de cada requisição.

## Estratégia de Resiliência
O sistema prioriza a continuidade do negócio acima da dependência externa. A validação de aptidão do associado (CPF) consulta uma API externa primária.

Devido à instabilidade inerente do serviço externo, foi implementado um mecanismo de **Fallback Local**:
1. O sistema tenta validar o CPF via API externa.
2. Em caso de falha (indisponibilidade ou timeout), a validação assume automaticamente um algoritmo local (Caelum Stella).
Isso garante que as assembleias e votações não sejam interrompidas por falhas em serviços de terceiros.

## Notas de Configuração e Segurança
O arquivo `application.yaml` contendo as credenciais de banco de dados foi intencionalmente versionado neste repositório.

Esta decisão visa facilitar a **avaliação técnica imediata** ("Clone and Run"), eliminando a necessidade de configuração de ambiente por parte do avaliador. Em um ambiente produtivo real, estas credenciais seriam injetadas via variáveis de ambiente ou gerenciadores de segredos (como AWS Secrets Manager), jamais expostas no controle de versão.

## Endpoints
A documentação interativa e especificação da API estão disponíveis no Swagger UI:

**URL Local:** [http://localhost:8080/docs](http://localhost:8080/docs)
