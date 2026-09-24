# TimeGrid Backend

Backend educativo de um sistema de agendamento, desenvolvido com Java e Spring Boot.

O projeto cobre um fluxo real de API REST com usuarios, clientes, agendamentos,
validacoes, tratamento global de excecoes, versionamento de banco com Flyway,
autenticacao com JWT e testes automatizados.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- PostgreSQL no perfil `dev`
- H2 no perfil `test`
- Lombok
- JUnit 5 e Mockito
- Swagger/OpenAPI com Springdoc

## Como rodar

Perfil ativo padrao:

```properties
spring.profiles.active=dev
```

No perfil `dev`, a aplicacao espera um PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/timegridDB
spring.datasource.username=postgres
spring.datasource.password=123456
```

Rodar os testes:

```bash
mvn test
```

Rodar a aplicacao:

```bash
mvn spring-boot:run
```

## Autenticacao

O login usa email e senha:

```http
POST /auth/login
```

Exemplo:

```json
{
  "email": "john.manager@timegrid.test",
  "password": "123456"
}
```

Resposta:

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

Use o access token nas rotas protegidas:

```http
Authorization: Bearer <accessToken>
```

Renovar o access token:

```http
POST /auth/refresh
```

```json
{
  "refreshToken": "..."
}
```

Regras atuais:

- access token: 60 minutos
- refresh token: 24 horas
- ao renovar, a API gera um novo access token e mantem o mesmo refresh token ate ele expirar
- depois de 24 horas, o usuario precisa fazer login novamente

## Autorizacao

Roles atuais:

- `MANAGER`: super usuario, pode acessar todos os endpoints, incluindo criacao de usuarios
- `ADMIN`: usuario comum autenticado, pode acessar os endpoints protegidos, exceto criacao de usuarios

Endpoints publicos:

- `POST /auth/login`
- `POST /auth/refresh`
- `/h2-console/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`

Endpoint restrito a `MANAGER`:

- `POST /users`

Demais endpoints exigem usuario autenticado via JWT.

## Endpoints principais

Usuarios:

```http
POST /users
GET /users
GET /users/{id}
PUT /users/{id}
DELETE /users/{id}
PATCH /users/{id}/active?active=true
```

Clientes:

```http
POST /users/{userId}/clients
GET /users/{userId}/clients
GET /users/{userId}/clients/{clientId}
PUT /users/{userId}/clients/{clientId}
DELETE /users/{userId}/clients/{clientId}
```

Agendamentos:

```http
POST /users/{userId}/appointments
GET /users/{userId}/appointments?date=2026-10-20
PUT /users/{userId}/appointments/{appointmentId}
DELETE /users/{userId}/appointments/{appointmentId}
```

## Swagger

Com a aplicacao rodando:

```text
http://localhost:8080/swagger-ui/index.html
```

Use o botao de autorizacao do Swagger para informar:

```text
Bearer <accessToken>
```

## CORS

Origem padrao liberada para desenvolvimento:

```properties
timegrid.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```

Para liberar mais de uma origem:

```bash
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://seu-front.com
```

## Variaveis de ambiente

```properties
JWT_SECRET=defina-um-segredo-forte-em-producao
JWT_EXPIRATION_MINUTES=60
JWT_REFRESH_EXPIRATION_MINUTES=1440
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

## Regras de negocio importantes

- usuario deletado sofre soft delete (`active=false`)
- apenas usuarios ativos aparecem na listagem
- cliente pertence a um usuario
- agendamento pertence a um usuario e a um cliente
- cliente precisa pertencer ao usuario informado no agendamento
- horarios encostados contam como conflito
- update de agendamento altera somente `endTime` e `service`

## Banco e Flyway

Migrations atuais:

- `V1__create_initial_schema.sql`
- `V2__seed_initial_data.sql`
- `V3__encode_seed_user_passwords.sql`

A `V3` converte as senhas seedadas `123456` para BCrypt. O login continua usando
a senha original `123456`; a hash fica apenas armazenada no banco.

## Testes

A suite cobre services, controllers, tratamento global de excecoes, autenticacao
e JWT.

Ultima verificacao:

```text
mvn test
BUILD SUCCESS
```
