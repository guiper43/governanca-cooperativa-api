# Governança Cooperativa API

API REST desenvolvida em **Java 21** com **Spring Boot 3** para gerenciamento de sessões de votação em cooperativas. Permite o cadastro de pautas, abertura de sessões de votação, recepção de votos de associados (com validação externa de CPF) e contabilização dos resultados.

> [!WARNING]
> **Aviso sobre Dependências Externas**
> O serviço externo de validação de CPF (`https://user-info.herokuapp.com/users/{cpf}`) encontra-se **indisponível/instável**.
> Para validar o fluxo de votação localmente, recomenda-se o uso de mocks ou a execução dos testes unitários que isolam essa dependência.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.x**
- **PostgreSQL** (Banco de dados)
- **Flyway** (Migração de dados)
- **Spring Cloud OpenFeign** (Integração externa)
- **Docker & Docker Compose**
- **JUnit 5 + Mockito** (Testes Unitários)
- **Lombok**

## 🛠️ Pré-requisitos

- Java JDK 21
- Docker e Docker Compose
- Maven (wrapper incluído no projeto)

## 🐳 Como Rodar

### 1. Subir a Infraestrutura
Utilize o Docker Compose para iniciar o banco de dados PostgreSQL:
```bash
docker-compose up -d
```

### 2. Executar a Aplicação
Com o banco rodando, execute a aplicação utilizando o Maven Wrapper:
```bash
./mvnw spring-boot:run
```
A API estará disponível em: `http://localhost:8080`

### 3. Solução de Contorno (Serviço Externo)
Como o validador de CPF está offline, utilize o profile de testes ou mocks para simular respostas:
- **Testes Unitários**: `./mvnw test` (validam a regra de negócio com mocks do Mockito).
- **Execução Local**: Se necessário, implementar um **Stub/WireMock** na porta da API externa para retornar `{ "status": "ABLE_TO_VOTE" }`.

## 🧪 Como Testar

### Testes Automatizados (Unitários)
O projeto conta com cobertura de testes unitários utilizando JUnit 5 e Mockito.
```bash
./mvnw test
```

### Testes Manuais de Endpoints (.http)
Para testar os endpoints via VS Code (Rest Client) ou IntelliJ (HTTP Client), utilize o arquivo de requisições incluído no projeto:
📍 **Caminho**: `src/main/java/br/com/guilherme/governanca_cooperativa_api/web/controller/api_testes.http`

Este arquivo contém exemplos prontos para:
1. Criar Pauta
2. Abrir Sessão
3. Votar (Cenários de Sucesso e Erro)
4. Consultar Resultados

## 📡 Endpoints Principais

### Pauta
- `POST /pautas`: Cria uma nova pauta.
- `GET /pautas/{id}`: Busca detalhes de uma pauta.

### Sessão
- `POST /pautas/{pautaId}/sessoes`: Abre uma sessão de votação para uma pauta.
  - *Body opcional*: `{ "duracaoMinutos": 10 }` (Default: 1 min).

### Voto
- `POST /pautas/{pautaId}/votos`: Registra um voto.
  - *Body*: `{ "associadoId": "CPF", "votoEscolha": "SIM/NAO" }`

### Resultado
- `GET /pautas/{pautaId}/resultado`: Exibe o resultado da votação (Aprovada/Reprovada/Empate).

## 🏗️ Padrões de Projeto

- **Arquitetura em Camadas**: Controller, Service, Repository, Entity.
- **DTOs (Records)**: Utilizados para entrada e saída de dados da API.
- **Tratamento de Erros Centralizado**: `GlobalExceptionHandler` mapeando exceções de negócio para status HTTP.
