# AGENTS.md

## Project: TimeGrid Backend

Backend de um sistema de agendamento desenvolvido em Java com Spring Boot. O projeto tem foco educativo e está evoluindo em camadas: entidades, repositories, services, regras de negócio, depois controllers/DTOs/autenticação.

Data deste contexto: 2026-05-17.

## Current Workspace

Root:
`D:\Projetos\Projetos-Java-Spring-Boot\timegrid-backend`

Main package:
`br.com.jhonnyazevedo.timegrid_backend`

Maven:
- `groupId`: `br.com.jhonnyazevedo`
- `artifactId`: `timegrid-backend`
- versão: `0.0.1-SNAPSHOT`

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
- Flyway está no `pom.xml`, mas ainda não há migrations em `src/main/resources`
- Spring AI Anthropic está no `pom.xml`, mas ainda não aparece usado no código

## Current Project Structure

Main folders:

- `appointment`
   - `entity`
   - `repository`
   - `service`
- `client`
   - `entity`
   - `repository`
   - `service`
- `user`
   - `entity`
   - `repository`
   - `service`
- `config`
- `enums`
- `exception`

Atualmente não há controllers REST, DTOs, mappers, autenticação JWT implementada, nem handlers globais de exceção.

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
- H2 em memória
- H2 console habilitado em `/h2-console`
- `spring.jpa.hibernate.ddl-auto=update`
- SQL formatado e exibido

`application-prod.properties`:
- existe, mas está vazio.

## Security

Classe:
`SecurityConfig`

Estado atual:
- CSRF desabilitado
- frame options desabilitado para permitir H2 console
- `/h2-console/**` liberado
- todas as demais rotas também estão liberadas com `anyRequest().permitAll()`

Importante:
- Spring Security está configurado, mas ainda não existe autenticação real.
- README cita JWT e senhas criptografadas, mas isso ainda não está implementado no código atual.

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
- `username` obrigatório e único
- `email` obrigatório e único
- `password` obrigatório
- `role` obrigatório, salvo como `EnumType.STRING`
- `active` obrigatório
- `createdAt` com `@CreationTimestamp`

Relacionamentos:
- `User` tem muitos `Appointment`
- `User` tem muitos `Client`
- ambos com `cascade = CascadeType.ALL` e `orphanRemoval = true`

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
- `name` obrigatório
- `phone` obrigatório
- `createdAt` com `@CreationTimestamp`

Relacionamentos:
- muitos clientes pertencem a um usuário
- `user` usa `@ManyToOne(fetch = FetchType.LAZY)`
- cliente tem muitos agendamentos

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
- `service` obrigatório
- `appointmentDate` obrigatório
- `startTime` obrigatório
- `endTime` obrigatório
- `startTime` e `endTime` salvos como `EnumType.STRING`
- constraint única em `user_id`, `appointmentDate`, `startTime`

Relacionamentos:
- muitos agendamentos pertencem a um usuário
- muitos agendamentos pertencem a um cliente
- ambos usam `@ManyToOne(fetch = FetchType.LAZY)`

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

Métodos:
- `Optional<User> findByEmail(String email)`
- `Optional<User> findByUsername(String username)`
- `boolean existsByEmail(String email)`
- `boolean existsByUsername(String username)`

### ClientRepository

Classe:
`client.repository.ClientRepository`

Extende:
`JpaRepository<Client, UUID>`

Métodos:
- `List<Client> findByUser(User user)`
- `boolean existsByUserAndPhone(User user, String phone)`

### AppointmentRepository

Classe:
`appointment.repository.AppointmentRepository`

Extende:
`JpaRepository<Appointment, UUID>`

Métodos:
- `List<Appointment> findByUserAndAppointmentDate(User user, LocalDate appointmentDate)`
- `boolean existsConflict(User user, LocalDate date, TimeGrid startTime, TimeGrid endTime, UUID appointmentId)`

A query `existsConflict` verifica conflito de intervalo de horário para o mesmo usuário e data.

A lógica atual usa:
- `a.startTime <= :endTime`
- `a.endTime >= :startTime`

Atenção futura: essa regra considera horários encostados como conflito. Exemplo: 08:00-09:00 e 09:00-10:00 podem conflitar. Se o comportamento desejado for permitir agendamentos colados, ajustar para comparação estrita.

## Services

### UserService

Interface:
`user.service.UserService`

Implementação:
`user.service.UserServiceImpl`

Métodos:
- `createUser(User user)`
- `findById(UUID id)`
- `listUsers()`
- `updateUser(UUID id, User user)`
- `deleteUser(UUID id)`
- `setActive(UUID id, Boolean active)`

Regras atuais:
- não permite email duplicado ao criar
- não permite username duplicado ao criar
- ao criar usuário, define `active = true`
- busca por ID lança `BusinessException` se não encontrar
- update altera username, email, password e role

Atenções:
- update ainda não valida duplicidade de email/username
- senha ainda é salva como texto recebido, sem criptografia
- delete usa `deleteById` direto

### ClientService

Interface:
`client.service.ClientService`

Implementação:
`client.service.ClientServiceImpl`

Métodos:
- `createClient(UUID userId, Client client)`
- `listByUser(UUID userId)`
- `findById(UUID id)`
- `updateClient(UUID id, Client client)`
- `deleteClient(UUID id)`

Regras atuais:
- cliente precisa pertencer a um usuário existente
- não permite cadastrar cliente com mesmo telefone para o mesmo usuário
- lista clientes por usuário
- update altera nome e telefone

Atenções:
- update ainda não valida duplicidade de telefone
- delete usa `deleteById` direto
- ainda não há validação via DTO

### AppointmentService

Interface:
`appointment.service.AppointmentService`

Implementação:
`appointment.service.AppointmentServiceImpl`

Métodos:
- `createAppointment(UUID userId, UUID clientId, Appointment appointment)`
- `updateAppointment(UUID userId, Appointment appointment)`
- `listAppointmentsByDate(UUID userId, LocalDate date)`
- `deleteAppointment(UUID id)`

Regras atuais de criação:
- `startTime`, `endTime` e `appointmentDate` são obrigatórios
- `startTime` não pode ser maior que `endTime`
- não permite agendamento em data passada
- usuário precisa existir
- cliente precisa existir
- cliente precisa pertencer ao usuário
- não permite conflito de horário usando `existsConflict`
- define `user` e `client` antes de salvar

Regras atuais de update:
- busca agendamento pelo ID do objeto recebido
- valida se o agendamento pertence ao usuário
- valida conflito de horário ignorando o próprio agendamento
- atualiza `endTime` e `service`

Atenções importantes:
- no update, a validação de horário usa os dados antigos de `existAppointment`, não os novos dados recebidos.
- no update, apenas `endTime` e `service` são atualizados; `startTime`, `appointmentDate` e `client` não mudam.
- no update, a checagem de conflito também usa data/horários antigos.
- delete não recebe `userId`, então ainda não garante que o agendamento deletado pertence ao usuário logado.

## Exception

Classe:
`exception.BusinessException`

Estado atual:
- `RuntimeException` simples com construtor recebendo `message`.

Ainda não existe:
- `@RestControllerAdvice`
- `@ExceptionHandler`
- padronização de resposta de erro
- status HTTP específico por tipo de erro

## Tests

Existe apenas:
`TimegridBackendApplicationTests`

Teste atual:
- `contextLoads()`

Ainda não há testes de services, repositories, regras de conflito de horário, segurança ou validações.

## Current Implementation Status

Implementado:
- entidades principais
- enums
- repositories
- services
- regras iniciais de negócio
- configuração básica de segurança
- perfis `dev` e `test`
- PostgreSQL no desenvolvimento
- H2 para teste
- constraint única parcial para agendamento por usuário/data/horário inicial

Ainda pendente:
- controllers REST
- DTOs de request/response
- Bean Validation nos DTOs
- mapper manual ou MapStruct
- autenticação e autorização reais
- JWT
- criptografia de senha com `PasswordEncoder`
- handler global de exceções
- migrations Flyway
- testes de regra de negócio
- revisão do `pom.xml` para remover dependências ainda não usadas
- configuração de produção
- documentação README alinhada ao estado real do código

## Development Rules For Future Agents

1. Manter acesso somente leitura, salvo se o usuário pedir explicitamente para editar arquivos.
2. Se for sugerir alterações, entregar o código no chat para o usuário copiar manualmente.
3. Antes de propor mudanças, ler o código atual e respeitar a estrutura existente.
4. Não assumir que JWT, controllers ou DTOs já existem.
5. Não prometer comportamento que ainda não está implementado.
6. Priorizar melhorias incrementais e educativas, explicando o motivo das mudanças.
7. Evitar refatorações grandes sem necessidade.
8. Preservar a divisão atual por domínio: `user`, `client`, `appointment`.
9. Preferir DTOs para entrada/saída de API quando os controllers forem criados.
10. Não colocar regras de negócio complexas dentro das entidades.
11. Usar `BusinessException` para regras de negócio até existir um tratamento global melhor.
12. Ao sugerir endpoints, usar os services existentes em vez de acessar repositories diretamente nos controllers.
13. Ao sugerir autenticação, implementar senha criptografada antes de qualquer login real.
14. Ao trabalhar com agendamentos, tomar cuidado com conflito de intervalo e pertencimento do cliente ao usuário.
15. Ao sugerir testes, começar pelos services, especialmente `AppointmentServiceImpl`.

## Suggested Next Steps

Prioridade 1:
- Criar DTOs para User, Client e Appointment.
- Criar controllers REST usando os services existentes.
- Criar `GlobalExceptionHandler` com `@RestControllerAdvice`.

Prioridade 2:
- Corrigir lógica de update de agendamento para validar e salvar os novos horários/data.
- Decidir se horários encostados devem ser permitidos.
- Proteger delete/update por `userId`.

Prioridade 3:
- Adicionar `PasswordEncoder`.
- Preparar fluxo de autenticação.
- Implementar JWT somente depois que senha criptografada e DTOs estiverem organizados.

Prioridade 4:
- Criar testes unitários/integrados dos services.
- Adicionar testes para conflito de horário.
- Adicionar testes para cliente que não pertence ao usuário.

Prioridade 5:
- Revisar dependências do `pom.xml`.
- Remover dependências ainda não usadas, se o objetivo for manter o projeto mais simples.
- Criar migrations Flyway quando o modelo estabilizar.