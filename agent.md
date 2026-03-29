# agent.md

## Project: TimeGrid Backend

This project is a scheduling system backend built with Spring Boot.

### Current Features
- User management
- Client management
- Appointment scheduling
- Time slot control using enums

### Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database (in-memory for development)
- Lombok

### Architecture
- entity: database models
- dto: data transfer objects (planned)
- repository: data access layer
- service: business logic
- controller: REST endpoints
- enums: fixed domain values

### Entities

#### User
- id (UUID)
- username (no spaces)
- email
- password
- role (UserRole)
- active
- createdAt
- relationships:
  - appointments
  - clients

#### Client
- id (UUID)
- name
- phone
- user (ManyToOne)
- appointments
- createdAt

#### Appointment
- id (UUID)
- user
- client
- service
- appointmentDate
- startTime (TimeGrid)
- endTime (TimeGrid)
- createdAt

### Constraints
- Unique constraint on (user_id, appointmentDate, startTime)

### Enums

#### TimeGrid
- 30-minute intervals from 08:00 to 22:00

#### UserRole
- Defines user permissions

### Database
- H2 in-memory database
- Auto schema generation (hibernate ddl-auto=update)

---

# rules.md

## Development Rules

1. Keep entities clean
   - Only database-related validations
   - Avoid business logic

2. Use DTOs for:
   - Validation
   - API input/output

3. Avoid using @Data in entities
   - Use @Getter and @Setter instead

4. Avoid using relationships in toString
   - Prevent lazy loading issues

5. Use enums for fixed values
   - Example: TimeGrid, UserRole

6. Use proper package structure
   - entity
   - dto
   - service
   - controller
   - repository
   - enums

7. Follow clean architecture principles

---

# context.md

## Development Context

This project is being developed in two phases:

1. Learning/Testing Version
   - Fast implementation
   - Validation inside entities allowed

2. Teaching Version (Clean)
   - Entities simplified
   - DTOs used for validation
   - Better architecture separation

## Current Status

- Enums created (TimeGrid, UserRole)
- Entities created (User, Client, Appointment)
- Relationships configured
- Unique constraints applied
- H2 database configured and tested
- Tables created successfully

## Next Steps

- Create DTOs
- Implement services
- Create controllers
- Add validation layer
- Prepare for production database (PostgreSQL)

---

# Commit Suggestion

feat: implement core entities and configure H2 in-memory database

- created User, Client, and Appointment entities
- configured relationships and constraints
- added enum integration (TimeGrid, UserRole)
- configured H2 database for development
- validated schema generation successfully

