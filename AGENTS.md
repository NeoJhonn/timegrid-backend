# AGENTS.md

## Project: TimeGrid Backend

Backend de um sistema de agendamento desenvolvido em Java com Spring Boot.
O projeto tem foco educativo e esta evoluindo em camadas: entidades,
repositories, services, regras de negocio, depois DTOs, mappers,
controllers, tratamento global de excecoes, testes e, por ultimo,
autenticacao/JWT.

Atualizado em: 2026-06-17.

## Current Workspace

Root:
`D:\Projetos\Projetos-Java-Spring-Boot\timegrid-backend`

Main package:
`br.com.jhonnyazevedo.timegrid_backend`

Maven:
- `groupId`: `br.com.jhonnyazevedo`
- `artifactId`: `timegrid-backend`
- versao: `0.0.1-SNAPSHOT`

## Tech Stack

- Java 21
- Spring Boot 4.0.4
- Maven
- Spring Data JPA
- Spring Security
- Spring Validation
- Lombok
- PostgreSQL no perfil `dev`
- H2 no perfil `test`
- Flyway esta no `pom.xml`, mas ainda nao ha migrations em `src/main/resources`
- O `pom.xml` foi limpo em 2026-05-18 para manter somente dependencias usadas
  ou planejadas no curto prazo.

## Current Project Structure

Main folders:

- `appointment`
  - `controller`
  - `dto`
  - `entity`
  - `mapper`
  - `repository`
  - `service`
- `client`
  - `controller`
  - `dto`
  - `entity`
  - `mapper`
  - `repository`
  - `service`
- `user`
  - `controller`
  - `dto`
  - `entity`
  - `mapper`
  - `repository`
  - `service`
- `config`
- `enums`
- `exception`

Ainda nao existem:
- autenticacao JWT real
- `PasswordEncoder`
- `@RestControllerAdvice`
- testes especificos de services/repositories

## Configuration

`application.properties`:
- `spring.application.name=timegrid-backend`
- perfil ativo atual: `dev`

`application-dev.properties`:
- PostgreSQL local
- database: `timegridDB`
- username: `postgres`
- password: `123456`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.show-sql=true`

`application-test.properties`:
- H2 em memoria
- H2 console habilitado em `/h2-console`
- `spring.jpa.hibernate.ddl-auto=update`
- SQL formatado e exibido

`application-prod.properties`:
- existe, mas esta vazio.

## Security

Classe:
`config.SecurityConfig`

Estado atual:
- CSRF desabilitado
- frame options desabilitado para permitir H2 console
- `/h2-console/**` liberado
- todas as demais rotas tambem estao liberadas com `anyRequest().permitAll()`

Importante:
- Spring Security esta configurado, mas ainda nao existe autenticacao real.
- JWT deve ficar para uma etapa posterior, depois de DTOs, controllers,
  exception handler, testes e senha criptografada.
- Antes de qualquer login real, implementar `PasswordEncoder`.

## POM / Dependencies

O `pom.xml` foi limpo em 2026-05-18.

Dependencias mantidas:
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- Flyway PostgreSQL support
- PostgreSQL runtime
- H2 runtime
- H2 console
- Lombok
- Devtools
- starters de teste relacionados a Web MVC, JPA, Security, Validation e Flyway

Dependencias removidas por nao estarem em uso:
- Spring Data JDBC
- Spring Data REST
- Spring GraphQL
- Spring RestClient
- Spring WebClient
- Spring AI Anthropic
- Spring AI BOM/dependency management
- testes relacionados a dependencias removidas
- Redis/session test starter

Observacao importante:
- Como `spring-boot-starter-data-rest` foi removido, os repositories nao sao
  mais expostos automaticamente como endpoints REST.
- Controllers REST proprios foram criados em 2026-06-17.
- Acessar `http://localhost:8080` diretamente ainda pode exibir Whitelabel/404,
  pois nao existe endpoint para `/`. Isso e esperado.

## Domain Model

### User

Classe:
`user.entity.User`

Campos:
- `UUID id`
- `String username`
- `String email`
- `String password`
- `UserRole role`
- `List<Appointment> appointments`
- `List<Client> clients`
- `Boolean active`
- `LocalDateTime createdAt`

Regras/constraints:
- tabela `users`
- `username` obrigatorio e unico
- `email` obrigatorio e unico
- `password` obrigatorio
- `role` obrigatorio, salvo como `EnumType.STRING`
- `active` obrigatorio
- `createdAt` com `@CreationTimestamp`

Relacionamentos:
- `User` tem muitos `Appointment`
- `User` tem muitos `Client`
- ambos com `cascade = CascadeType.ALL` e `orphanRemoval = true`

Regra importante:
- delete de usuario agora e soft delete: `active=false`.
- `listUsers()` lista somente usuarios ativos.

### Client

Classe:
`client.entity.Client`

Campos:
- `UUID id`
- `String name`
- `String phone`
- `User user`
- `List<Appointment> appointments`
- `LocalDateTime createdAt`

Regras/constraints:
- tabela `clients`
- `name` obrigatorio
- `phone` obrigatorio
- `createdAt` com `@CreationTimestamp`

Relacionamentos:
- muitos clientes pertencem a um usuario
- `user` usa `@ManyToOne(fetch = FetchType.LAZY)`
- cliente tem muitos agendamentos

Regra importante:
- delete de cliente agora recebe `userId` e `clientId` e valida se o cliente
  pertence ao usuario antes de deletar.
- Deletar cliente pode impactar agendamentos por causa do relacionamento com
  cascade/orphan removal. Confirmar regra de negocio antes de expor endpoint.

### Appointment

Classe:
`appointment.entity.Appointment`

Campos:
- `UUID id`
- `User user`
- `Client client`
- `String service`
- `LocalDate appointmentDate`
- `TimeGrid startTime`
- `TimeGrid endTime`
- `LocalDateTime createdAt`

Regras/constraints:
- tabela `appointments`
- `service` obrigatorio
- `appointmentDate` obrigatorio
- `startTime` obrigatorio
- `endTime` obrigatorio
- `startTime` e `endTime` salvos como `EnumType.STRING`
- constraint unica em `user_id`, `appointmentDate`, `startTime`

Relacionamentos:
- muitos agendamentos pertencem a um usuario
- muitos agendamentos pertencem a um cliente
- ambos usam `@ManyToOne(fetch = FetchType.LAZY)`

Regra importante de update:
- `updateAppointment(UUID userId, Appointment appointment)` atualiza somente:
  - `endTime`
  - `service`
- `startTime`, `appointmentDate`, `client` e `user` nao sao alterados no update.
- A validacao usa os dados existentes do banco para usuario, data e horario
  inicial, e usa o novo `endTime` recebido.

## Enums

### TimeGrid

Classe:
`enums.TimeGrid`

Representa intervalos de 30 minutos entre 08:00 e 22:00.

Valores:
- `T0800`
- `T0830`
- `T0900`
- `T0930`
- `T1000`
- `T1030`
- `T1100`
- `T1130`
- `T1200`
- `T1230`
- `T1300`
- `T1330`
- `T1400`
- `T1430`
- `T1500`
- `T1530`
- `T1600`
- `T1630`
- `T1700`
- `T1730`
- `T1800`
- `T1830`
- `T1900`
- `T1930`
- `T2000`
- `T2030`
- `T2100`
- `T2130`
- `T2200`

Regra de negocio decidida:
- horarios encostados devem ser considerados conflito.
- Exemplo: se existe `09:00-10:00`, o proximo horario permitido e `10:30`.
- Portanto, manter a query de conflito com comparacao inclusiva:
  - `a.startTime <= :endTime`
  - `a.endTime >= :startTime`

### UserRole

Classe:
`enums.UserRole`

Valores:
- `ADMIN`
- `MANAGER`

## Repositories

### UserRepository

Classe:
`user.repository.UserRepository`

Extende:
`JpaRepository<User, UUID>`

Metodos:
- `Optional<User> findByEmail(String email)`
- `Optional<User> findByUsername(String username)`
- `boolean existsByEmail(String email)`
- `boolean existsByUsername(String username)`
- `List<User> findByActiveTrue()`

### ClientRepository

Classe:
`client.repository.ClientRepository`

Extende:
`JpaRepository<Client, UUID>`

Metodos:
- `List<Client> findByUser(User user)`
- `boolean existsByUserAndPhone(User user, String phone)`
- `Optional<Client> findByUserAndPhone(User user, String phone)`

Uso atual:
- `existsByUserAndPhone` e usado no create.
- `findByUserAndPhone` e usado no update para validar duplicidade ignorando
  o proprio cliente.

### AppointmentRepository

Classe:
`appointment.repository.AppointmentRepository`

Extende:
`JpaRepository<Appointment, UUID>`

Metodos:
- `List<Appointment> findByUserAndAppointmentDate(User user, LocalDate appointmentDate)`
- `boolean existsConflict(User user, LocalDate date, TimeGrid startTime, TimeGrid endTime, UUID appointmentId)`

A query `existsConflict` verifica conflito de intervalo para o mesmo usuario
e data. A regra atual considera horarios encostados como conflito.

## DTOs And Manual Mappers

DTOs e mappers manuais foram criados em 2026-06-12.

Padrao adotado:
- DTOs implementados como Java `record`.
- Validacao de entrada com Jakarta Bean Validation nos request DTOs.
- Mappers manuais anotados com `@Component`.
- Response DTOs nao expõem senha.
- Mappers convertem request DTO para entidade e entidade para response DTO.
- Controllers REST foram criados em 2026-06-17 usando DTOs, mappers e services.

### User DTOs

Pacote:
`user.dto`

Arquivos:
- `UserRequest`
- `UserResponse`

`UserRequest` contem:
- `username`
- `email`
- `password`
- `role`

Validacoes:
- `username`: `@NotBlank`
- `email`: `@NotBlank` e `@Email`
- `password`: `@NotBlank`
- `role`: `@NotNull`

`UserResponse` contem:
- `id`
- `username`
- `email`
- `role`
- `active`
- `createdAt`

Importante:
- `UserResponse` nao retorna `password`.

Mapper:
`user.mapper.UserMapper`

Metodos:
- `User toEntity(UserRequest request)`
- `UserResponse toResponse(User user)`
- `List<UserResponse> toResponseList(List<User> users)`

### Client DTOs

Pacote:
`client.dto`

Arquivos:
- `ClientRequest`
- `ClientResponse`

`ClientRequest` contem:
- `name`
- `phone`

Validacoes:
- `name`: `@NotBlank`
- `phone`: `@NotBlank`

`ClientResponse` contem:
- `id`
- `name`
- `phone`
- `userId`
- `createdAt`

Mapper:
`client.mapper.ClientMapper`

Metodos:
- `Client toEntity(ClientRequest request)`
- `ClientResponse toResponse(Client client)`
- `List<ClientResponse> toResponseList(List<Client> clients)`

### Appointment DTOs

Pacote:
`appointment.dto`

Arquivos:
- `AppointmentRequest`
- `AppointmentUpdateRequest`
- `AppointmentResponse`

`AppointmentRequest` contem:
- `clientId`
- `service`
- `appointmentDate`
- `startTime`
- `endTime`

Validacoes:
- `clientId`: `@NotNull`
- `service`: `@NotBlank`
- `appointmentDate`: `@NotNull` e `@FutureOrPresent`
- `startTime`: `@NotNull`
- `endTime`: `@NotNull`

`AppointmentUpdateRequest` reflete a regra atual de update e contem somente:
- `endTime`
- `service`

Validacoes:
- `endTime`: `@NotNull`
- `service`: `@NotBlank`

`AppointmentResponse` contem:
- `id`
- `userId`
- `clientId`
- `clientName`
- `service`
- `appointmentDate`
- `startTime`
- `endTime`
- `createdAt`

Mapper:
`appointment.mapper.AppointmentMapper`

Metodos:
- `Appointment toEntity(AppointmentRequest request)`
- `Appointment toEntity(UUID appointmentId, AppointmentUpdateRequest request)`
- `AppointmentResponse toResponse(Appointment appointment)`
- `List<AppointmentResponse> toResponseList(List<Appointment> appointments)`

Build verificado:
- `mvn test` executado em 2026-06-12 com sucesso.
- Resultado: `BUILD SUCCESS`, `Tests run: 1, Failures: 0, Errors: 0`.

## Controllers REST

Controllers REST foram criados em 2026-06-17.

Padrao adotado:
- Controllers recebem request DTOs com `@RequestBody @Valid`.
- Controllers usam mappers manuais para converter DTO para entidade.
- Controllers chamam os services existentes.
- Controllers retornam response DTOs.
- Controllers nao possuem `try/catch` local para regras de negocio.
- Tratamento de excecoes deve ser centralizado futuramente com
  `@RestControllerAdvice`.

### UserController

Classe:
`user.controller.UserController`

Base path:
`/users`

Endpoints:
- `POST /users`
- `GET /users`
- `GET /users/{id}`
- `PUT /users/{id}`
- `DELETE /users/{id}`
- `PATCH /users/{id}/active?active=true`

Observacoes:
- Usa `UserRequest`, `UserResponse` e `UserMapper`.
- `DELETE /users/{id}` chama soft delete via `UserService.deleteUser`.
- `PATCH /users/{id}/active` chama `UserService.setActive`.

### ClientController

Classe:
`client.controller.ClientController`

Endpoints:
- `POST /users/{userId}/clients`
- `GET /users/{userId}/clients`
- `GET /clients/{id}`
- `PUT /clients/{id}`
- `DELETE /users/{userId}/clients/{clientId}`

Observacoes:
- Usa `ClientRequest`, `ClientResponse` e `ClientMapper`.
- Create/list/delete carregam `userId` no path porque o service atual depende do
  usuario para validar pertencimento e escopo.
- `DELETE /users/{userId}/clients/{clientId}` valida pertencimento no service.

### AppointmentController

Classe:
`appointment.controller.AppointmentController`

Endpoints:
- `POST /users/{userId}/appointments`
- `GET /users/{userId}/appointments?date=yyyy-MM-dd`
- `PUT /users/{userId}/appointments/{appointmentId}`
- `DELETE /appointments/{appointmentId}`

Observacoes:
- Usa `AppointmentRequest`, `AppointmentUpdateRequest`,
  `AppointmentResponse` e `AppointmentMapper`.
- Create recebe `clientId` pelo `AppointmentRequest`.
- Listagem por data usa query param `date` com formato ISO, exemplo:
  `?date=2026-06-17`.
- Update respeita a regra de negocio atual: altera somente `endTime` e
  `service`.
- Delete de appointment ainda nao recebe `userId`, pois o service atual ainda e
  `deleteAppointment(UUID id)`. Melhorar futuramente para validar pertencimento.

Build verificado:
- `mvn test` executado em 2026-06-17 com sucesso.
- Resultado: `BUILD SUCCESS`, `Tests run: 1, Failures: 0, Errors: 0`.

## Exception Handling Direction

Decisao tomada em 2026-06-17:
- Nao adicionar `try/catch` repetido dentro dos controllers.
- Proximo passo deve ser criar um `GlobalExceptionHandler` com
  `@RestControllerAdvice`.

Tratamentos sugeridos para o `GlobalExceptionHandler`:
- `BusinessException`
- erros de Bean Validation dos DTOs
- parametros invalidos, como UUID ou data mal formatada
- fallback generico para `Exception`

Formato esperado futuramente:
- respostas JSON padronizadas
- status HTTP adequado por tipo de erro
- mensagens de regra de negocio vindas de `BusinessException`

## Services

### UserService

Interface:
`user.service.UserService`

Implementacao:
`user.service.UserServiceImpl`

Metodos:
- `createUser(User user)`
- `findById(UUID id)`
- `listUsers()`
- `updateUser(UUID id, User user)`
- `deleteUser(UUID id)`
- `setActive(UUID id, Boolean active)`

Regras atuais:
- nao permite email duplicado ao criar
- nao permite username duplicado ao criar
- ao criar usuario, define `active = true`
- busca por ID lanca `BusinessException` se nao encontrar
- `listUsers()` retorna somente usuarios ativos via `findByActiveTrue()`
- `updateUser` valida `username` e `email` obrigatorios
- `updateUser` valida duplicidade de `email` e `username`, ignorando o proprio usuario
- `updateUser` altera username, email, password e role
- `deleteUser` nao apaga fisicamente; define `active=false`

Atencoes:
- senha ainda e salva como texto recebido, sem criptografia
- quando implementar autenticacao, adicionar `PasswordEncoder` antes de login/JWT
- avaliar validacoes de `password` e `role` no update/criacao quando DTOs forem criados

### ClientService

Interface:
`client.service.ClientService`

Implementacao:
`client.service.ClientServiceImpl`

Metodos:
- `createClient(UUID userId, Client client)`
- `listByUser(UUID userId)`
- `findById(UUID id)`
- `updateClient(UUID id, Client client)`
- `deleteClient(UUID userId, UUID clientId)`

Regras atuais:
- cliente precisa pertencer a um usuario existente
- nao permite cadastrar cliente com mesmo telefone para o mesmo usuario
- lista clientes por usuario
- `updateClient` valida `name` e `phone` obrigatorios
- `updateClient` valida duplicidade de telefone para o mesmo usuario,
  ignorando o proprio cliente
- `updateClient` altera nome e telefone
- `deleteClient` valida se o cliente pertence ao usuario antes de deletar

Atencoes:
- ainda nao ha DTOs/Bean Validation
- avaliar se deletar cliente deve apagar agendamentos ou se deve bloquear
  delete quando houver historico

### AppointmentService

Interface:
`appointment.service.AppointmentService`

Implementacao:
`appointment.service.AppointmentServiceImpl`

Metodos:
- `createAppointment(UUID userId, UUID clientId, Appointment appointment)`
- `updateAppointment(UUID userId, Appointment appointment)`
- `listAppointmentsByDate(UUID userId, LocalDate date)`
- `deleteAppointment(UUID id)`

Regras atuais de criacao:
- `startTime`, `endTime` e `appointmentDate` sao obrigatorios
- `startTime` nao pode ser maior que `endTime`
- nao permite agendamento em data passada
- usuario precisa existir
- cliente precisa existir
- cliente precisa pertencer ao usuario
- nao permite conflito de horario usando `existsConflict`
- define `user` e `client` antes de salvar

Regras atuais de update:
- busca agendamento pelo ID do objeto recebido
- valida se o agendamento pertence ao usuario recebido por parametro
- exige novo `endTime`
- exige `service` nao nulo e nao vazio
- valida se o `startTime` existente no banco nao e maior que o novo `endTime`
- valida conflito usando usuario/data/startTime existentes e novo `endTime`
- atualiza somente `endTime` e `service`

Atencoes importantes:
- `deleteAppointment(UUID id)` ainda nao recebe `userId`, entao ainda nao garante
  que o agendamento deletado pertence ao usuario logado.
- Futuramente, considerar mudar para `deleteAppointment(UUID userId, UUID appointmentId)`.

## Exception

Classe:
`exception.BusinessException`

Estado atual:
- `RuntimeException` simples com construtor recebendo `message`.

Ainda nao existe:
- `@RestControllerAdvice`
- `@ExceptionHandler`
- padronizacao de resposta de erro
- status HTTP especifico por tipo de erro

## Tests

Existe apenas:
`TimegridBackendApplicationTests`

Teste atual:
- `contextLoads()`

Ainda nao ha testes de:
- services
- repositories
- conflito de horario
- horarios encostados como conflito
- cliente que nao pertence ao usuario
- soft delete de usuario
- delete de cliente com pertencimento
- seguranca/autenticacao

## Current Implementation Status

Implementado:
- entidades principais
- enums
- repositories
- services
- DTOs de request/response
- mappers manuais
- controllers REST
- regras iniciais de negocio
- validacoes adicionais em updates
- soft delete de usuario
- delete de cliente com validacao de pertencimento
- listagem de usuarios ativos
- configuracao basica de seguranca liberando tudo
- perfis `dev` e `test`
- PostgreSQL no desenvolvimento
- H2 para teste
- constraint unica parcial para agendamento por usuario/data/horario inicial
- `pom.xml` limpo, sem dependencias nao usadas como Data REST, GraphQL,
  WebClient, RestClient, JDBC e Spring AI

Ainda pendente:
- handler global de excecoes
- `PasswordEncoder`
- autenticacao e autorizacao reais
- JWT
- migrations Flyway
- testes de regra de negocio
- configuracao de producao
- documentacao README alinhada ao estado real do codigo

## Recommended Next Path

Ordem recomendada para continuar:

1. Criar `GlobalExceptionHandler` com `@RestControllerAdvice`.
2. Criar testes dos services, principalmente regras de negocio.
3. Adicionar `PasswordEncoder`.
4. Preparar fluxo de autenticacao.
5. Implementar JWT somente depois que o restante estiver estavel.

Organizacao sugerida:

```text
user/
  dto/
  mapper/
  entity/
  repository/
  service/

client/
  dto/
  mapper/
  entity/
  repository/
  service/

appointment/
  dto/
  mapper/
  entity/
  repository/
  service/
```

Fluxo desejado:

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

Sugestao educativa:
- proxima aula deve focar no `GlobalExceptionHandler`
- depois disso, iniciar testes pelos services

## Development Rules For Future Agents

1. Manter acesso somente leitura, salvo se o usuario pedir explicitamente para editar arquivos.
2. Antes de propor mudancas, ler o codigo atual e respeitar a estrutura existente.
3. Nao assumir que JWT ou handler global de excecoes ja existem.
4. Nao prometer comportamento que ainda nao esta implementado.
5. Priorizar melhorias incrementais e educativas, explicando o motivo das mudancas.
6. Evitar refatoracoes grandes sem necessidade.
7. Preservar a divisao atual por dominio: `user`, `client`, `appointment`.
8. Usar os DTOs criados para entrada/saida de API nos controllers existentes.
9. Usar os mappers manuais ja criados, mantendo o aprendizado claro.
10. Nao colocar regras de negocio complexas dentro das entidades.
11. Usar `BusinessException` para regras de negocio ate existir tratamento global melhor.
12. Ao sugerir endpoints, usar os services existentes em vez de acessar repositories diretamente nos controllers.
13. Ao sugerir autenticacao, implementar senha criptografada antes de qualquer login real.
14. Ao trabalhar com agendamentos, respeitar a regra de que horarios encostados sao conflito.
15. Ao trabalhar com update de agendamento, lembrar que ele altera somente `endTime` e `service`.
16. Ao sugerir delete de appointment, preferir adicionar `userId` para validar pertencimento.
17. Ao sugerir testes, comecar pelos services, especialmente `AppointmentServiceImpl`.
18. JWT deve ficar para depois de DTOs, mappers, controllers, exception handler e testes basicos.
19. Nao espalhar `try/catch` pelos controllers; centralizar tratamento com `@RestControllerAdvice`.
