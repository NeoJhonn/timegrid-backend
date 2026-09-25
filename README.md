# 🗓️ TimeGrid

TimeGrid é uma aplicação de agendamento desenvolvida com foco educativo, com o objetivo de ensinar conceitos reais de desenvolvimento backend utilizando Spring Boot.

O projeto simula um sistema de agenda onde usuários podem cadastrar clientes e gerenciar agendamentos de forma organizada e segura.

A API backend foi finalizada com controllers REST, DTOs, mappers manuais, regras de negócio, tratamento global de exceções, versionamento de banco com Flyway, autenticação com JWT, refresh token, autorização por roles, CORS, Swagger/OpenAPI e testes automatizados.

---

## 🚀 Tecnologias utilizadas

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- JWT (JSON Web Token)
- PostgreSQL
- H2 para testes
- Flyway
- Lombok
- JUnit 5
- Mockito
- Swagger/OpenAPI com Springdoc

---

## 📖 Objetivo do projeto

Este projeto foi criado com foco em aprendizado prático, abordando:

- Estruturação de um backend real
- Modelagem de dados (MER)
- Implementação de regras de negócio
- Criação de uma API REST em camadas
- Uso de DTOs e mappers manuais
- Validação de dados no backend
- Tratamento global de exceções
- Versionamento de banco com Flyway
- Autenticação com JWT
- Refresh token
- Autorização por roles
- Configuração de CORS
- Documentação com Swagger/OpenAPI
- Testes automatizados com JUnit 5 e Mockito
- Boas práticas com Spring Boot

---

## 📊 Modelo de Dados (MER)

Abaixo está o modelo entidade-relacionamento da aplicação:

![MER](./assets/Diagrama%20sem%20App%20Agenda.png)

---

## 🧠 Regras de Negócio

A aplicação segue algumas regras essenciais para garantir a consistência dos dados:

### ⛔ 1. Não permitir conflito de horário

Um usuário não pode ter dois agendamentos conflitantes no mesmo dia.

A regra atual considera horários encostados como conflito.

Exemplo:

- Se existe um agendamento das `09:00` até `10:00`
- O próximo horário permitido começa em `10:30`

---

### ⏰ 2. Limite de horário

Os agendamentos só podem ser feitos dentro do intervalo:

- Início: 08:00
- Fim: 22:00

Os horários são representados pelo enum `TimeGrid`, com intervalos de 30 minutos.

---

### 👤 3. Cliente pertence ao usuário

Um usuário só pode agendar horários para seus próprios clientes.

O backend valida esse pertencimento antes de criar, buscar, atualizar ou remover registros relacionados.

---

### 🧾 4. Atualização de agendamento

Ao atualizar um agendamento, a API altera somente:

- Serviço
- Horário final

O usuário, o cliente, a data e o horário inicial do agendamento não são alterados no update.

---

### 🧍 5. Usuário inativo

O delete de usuário é um soft delete.

Ao remover um usuário, o sistema define:

```text
active=false
```

Usuários inativos não aparecem na listagem e não conseguem fazer login.

---

## ✅ Requisitos Funcionais

- Cadastro de usuário
- Login com email e senha
- Geração de access token JWT
- Geração de refresh token
- Renovação de access token com refresh token
- Cadastro de clientes
- Listagem de clientes por usuário
- Suporte ao autocomplete de clientes no agendamento por meio da listagem de clientes do usuário
- Busca de clientes por usuário
- Atualização de clientes
- Exclusão de clientes com validação de pertencimento
- Criação de agendamentos
- Listagem de agendamentos por data
- Atualização parcial de agendamentos
- Exclusão de agendamentos com validação de pertencimento
- Validação de regras de negócio no backend
- Documentação dos endpoints com Swagger/OpenAPI

---

## 🔒 Requisitos Não Funcionais

- Segurança com autenticação JWT
- Senhas criptografadas com BCrypt
- Autorização por roles
- Sessão stateless
- Validação de dados no backend
- Tratamento global de exceções
- Integridade relacional com banco de dados
- Versionamento de schema com Flyway
- Configuração de CORS para integração com frontend
- Estrutura organizada em camadas (Controller, DTO, Mapper, Service, Repository)
- Testes automatizados para services, controllers, autenticação, JWT e exceptions

---

## 🔐 Autenticação e Autorização

O login usa email e senha:

```http
POST /auth/login
```

Exemplo de request:

```json
{
  "email": "john.manager@timegrid.test",
  "password": "123456"
}
```

Exemplo de response:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

O access token deve ser enviado nas rotas protegidas:

```http
Authorization: Bearer <accessToken>
```

Para renovar o access token:

```http
POST /auth/refresh
```

Exemplo:

```json
{
  "refreshToken": "..."
}
```

Regras atuais:

- Access token expira em 60 minutos por padrão
- Refresh token expira em 24 horas por padrão
- O refresh gera um novo access token
- O refresh token original é mantido até expirar
- Usuários inativos não conseguem fazer login nem renovar token

### Roles

- `MANAGER`: super usuário, pode acessar todos os endpoints, incluindo criação de usuários
- `ADMIN`: usuário autenticado, acessa rotas protegidas, exceto criação de usuários

Endpoints públicos:

- `POST /auth/login`
- `POST /auth/refresh`
- `/h2-console/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`

Endpoint restrito a `MANAGER`:

- `POST /users`

Demais endpoints exigem autenticação via JWT.

---

## 🧭 Endpoints principais

### Usuários

```http
POST /users
GET /users
GET /users/{id}
PUT /users/{id}
DELETE /users/{id}
PATCH /users/{id}/active?active=true
```

### Clientes

```http
POST /users/{userId}/clients
GET /users/{userId}/clients
GET /users/{userId}/clients/{clientId}
PUT /users/{userId}/clients/{clientId}
DELETE /users/{userId}/clients/{clientId}
```

### Agendamentos

```http
POST /users/{userId}/appointments
GET /users/{userId}/appointments?date=2026-10-20
PUT /users/{userId}/appointments/{appointmentId}
DELETE /users/{userId}/appointments/{appointmentId}
```

---

## 🏗️ Estrutura do Projeto

```text
src/main/java/br/com/jhonnyazevedo/timegrid_backend
├── appointment
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── auth
│   ├── controller
│   ├── dto
│   └── service
├── client
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── config
├── enums
├── exception
└── user
    ├── controller
    ├── dto
    ├── entity
    ├── mapper
    ├── repository
    └── service
```

Fluxo principal da API:

```text
Front-end
  -> Controller
  -> Request DTO
  -> Mapper
  -> Entity
  -> Service
  -> Repository
  -> Banco

Banco
  -> Entity
  -> Service
  -> Mapper
  -> Response DTO
  -> Controller
  -> Front-end
```

---

## 🗄️ Banco de Dados e Flyway

No perfil `dev`, a aplicação usa PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/timegridDB
spring.datasource.username=postgres
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=validate
```

As migrations ficam em:

```text
src/main/resources/db/migration
```

Migrations atuais:

- `V1__create_initial_schema.sql`
- `V2__seed_initial_data.sql`
- `V3__encode_seed_user_passwords.sql`

A `V3` converte as senhas seedadas em texto puro para BCrypt.

O login manual continua usando a senha original:

```text
123456
```

---

## 📘 Swagger/OpenAPI

Com a aplicação rodando, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

Para testar rotas protegidas pelo Swagger, use o botão de autorização e informe:

```text
Bearer <accessToken>
```

---

## 🌐 CORS

A origem padrão liberada para desenvolvimento é:

```properties
http://localhost:4200
```

Configuração:

```properties
timegrid.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```

Para liberar mais de uma origem:

```bash
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://seu-front.com
```

---

## ⚙️ Como rodar o projeto

Perfil ativo padrão:

```properties
spring.profiles.active=dev
```

Rodar os testes:

```bash
mvn test
```

Rodar a aplicação:

```bash
mvn spring-boot:run
```

Variáveis de ambiente importantes:

```properties
JWT_SECRET=defina-um-segredo-forte-em-producao
JWT_EXPIRATION_MINUTES=60
JWT_REFRESH_EXPIRATION_MINUTES=1440
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

---

## 🧪 Testes

A suíte de testes cobre:

- Services
- Controllers
- Tratamento global de exceções
- Autenticação
- JWT
- Refresh token

Última verificação:

```text
mvn test
BUILD SUCCESS
Tests run: 73, Failures: 0, Errors: 0
```

Ainda não há testes específicos de repositories.

---

## 📌 Status atual

Implementado:

- Entidades principais
- Repositories
- Services
- DTOs de request e response
- Mappers manuais
- Controllers REST
- Tratamento global de exceções
- Regras iniciais de negócio
- Soft delete de usuário
- Validação de pertencimento entre usuário, cliente e agendamento
- Autenticação com JWT
- Refresh token
- Senhas criptografadas com BCrypt
- Autorização por roles
- CORS
- Swagger/OpenAPI
- Flyway
- Testes automatizados

Pendente ou futuro:

- Configuração de produção
- Testes de repositories, se alguma regra passar a depender de comportamento real do banco
- Frontend em Angular

---

## 🎯 Próxima etapa

Com a API backend finalizada, a próxima etapa natural do projeto é iniciar o frontend em Angular com Tailwind, consumindo os endpoints protegidos por JWT.
