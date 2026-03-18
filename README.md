# 🗓️ TimeGrid

TimeGrid é uma aplicação de agendamento desenvolvida com foco educativo, com o objetivo de ensinar conceitos reais de desenvolvimento backend utilizando Spring Boot.

O projeto simula um sistema de agenda onde usuários podem cadastrar clientes e gerenciar agendamentos de forma organizada e segura.

---

## 🚀 Tecnologias utilizadas

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT (JSON Web Token)
- PostgreSQL
- Lombok

---

## 📖 Objetivo do projeto

Este projeto foi criado com foco em aprendizado prático, abordando:

- Estruturação de um backend real
- Modelagem de dados (MER)
- Implementação de regras de negócio
- Autenticação com JWT
- Boas práticas com Spring Boot

---

## 📊 Modelo de Dados (MER)

Abaixo está o modelo entidade-relacionamento da aplicação:

![MER](./assets/Diagrama%20sem%20App%20Agenda.png)

> 📌 Substitua o caminho acima pelo local correto da imagem no seu repositório.

---

## 🧠 Regras de Negócio

A aplicação segue algumas regras essenciais para garantir a consistência dos dados:

### ⛔ 1. Não permitir conflito de horário
Um usuário não pode ter dois agendamentos no mesmo horário.

---

### ⏰ 2. Limite de horário
Os agendamentos só podem ser feitos dentro do intervalo:

- Início: 08:00
- Fim: 22:00

---

### 👤 3. Cliente pertence ao usuário
Um usuário só pode agendar horários para seus próprios clientes.

---

## ✅ Requisitos Funcionais

- Cadastro de usuário
- Autenticação com login
- Geração de token JWT
- Cadastro de clientes
- Listagem de clientes por usuário
- Autocomplete de clientes no agendamento
- Criação de agendamentos
- Validação de regras de negócio no backend

---

## 🔒 Requisitos Não Funcionais

- Segurança com autenticação JWT
- Senhas criptografadas
- Validação de dados no backend
- Integridade relacional com banco de dados
- Estrutura organizada em camadas (Controller, Service, Repository)

---

## 🏗️ Estrutura do Projeto
