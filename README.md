<div align="center">

# ⚙️ TaskManager — Back-End RESTful API

**API RESTful de Alta Performance, Segura e Padrão Corporativo em Java 25 & Spring Boot 3**

![Java 25](https://img.shields.io/badge/Java-25_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3.4.3](https://img.shields.io/badge/Spring_Boot-3.4.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL 8.4](https://img.shields.io/badge/MySQL-8.4_LTS-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![OpenAPI 3.0](https://img.shields.io/badge/OpenAPI-3.0_/_Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![JUnit 5](https://img.shields.io/badge/JUnit-5.11-25A162?style=for-the-badge&logo=junit5&logoColor=white)

</div>

---

## 📖 Visão Geral

O **TaskManager Back-End** é uma API RESTful robusta, de alta performance e construída sob os princípios da **Arquitetura Limpa em Camadas**, desenvolvida utilizando os recursos modernos do **Java 25** e do **Spring Boot 3.4.3**.

A API foi projetada para ser **100% pública**, fornecendo o ciclo de vida completo de gerenciamento de tarefas (*CRUD*), pesquisas avançadas com paginação e ordenação dinâmica, sem a sobrecarga de mecanismos de autenticação (como JWT, OAuth2 ou sessões).

> 🔓 **Arquitetura Aberta por Design**: Todos os endpoints da API estão abertos para consumo direto, ideais para integração com interfaces web, dispositivos móveis ou proxies reversos.

---

## 🏛️ Arquitetura e Organização de Pacotes

A aplicação segue rigorosamente o padrão de responsabilidade única e desacoplamento de camadas:

```text
Controller (REST API) ──► Service (Regras de Negócio) ──► Repository (JPA Specs) ──► Entity (JPA)
                                   │
                                   ▼
                              Mapper ──► DTO (Java Records)
```

### Estrutura de Pacotes (`com.example.todo`)

```text
backend/
├── pom.xml                 # Gerenciamento de dependências e plugins Maven
├── mvnw.cmd                # Maven Wrapper para execução determinística no Windows
├── src/
│   ├── main/
│   │   ├── java/com/example/todo/
│   │   │   ├── TodoApplication.java       # Ponto de entrada da aplicação Spring Boot
│   │   │   ├── config/                    # Configurações de CORS e Springdoc OpenAPI
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/                # Controladores REST finos (HTTP Handlers)
│   │   │   │   └── TaskController.java
│   │   │   ├── domain/                    # Entidades de banco e Enums com Converters JPA
│   │   │   │   ├── entity/Task.java
│   │   │   │   └── enums/ (TaskStatus, TaskPriority, TaskStatusConverter, TaskPriorityConverter)
│   │   │   ├── dto/                       # Objetos de Transferência de Dados em Java Records
│   │   │   │   ├── common/ (ApiErrorResponse, FieldErrorDetail, PageResponse)
│   │   │   │   └── task/ (TaskRequest, TaskResponse, TaskStatusUpdateRequest)
│   │   │   ├── exception/                 # Tratamento global de exceções via @RestControllerAdvice
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidParamException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── mapper/                    # Mapeadores explícitos de transferência (sem MapStruct)
│   │   │   │   └── TaskMapper.java
│   │   │   ├── repository/                # Repositório Spring Data JPA com JpaSpecificationExecutor
│   │   │   │   └── TaskRepository.java
│   │   │   └── service/                   # Camada de Serviços e Regras de Negócio Transacionais
│   │   │       └── TaskService.java
│   │   └── resources/
│   │       ├── application.yml            # Configuração padrão do Spring Boot
│   │       └── application-dev.yml        # Configuração do perfil dev (MySQL validate mode)
│   └── test/                              # Suíte de testes unitários e de integração
│       └── java/com/example/todo/
│           ├── controller/TaskControllerTest.java
│           ├── integration/TaskRepositoryIntegrationTest.java
│           ├── mapper/TaskMapperTest.java
│           └── service/TaskServiceTest.java
```

---

## 🛠️ Matriz Tecnológica

| Componente | Tecnologia | Versão | Justificativa / Descrição |
|---|---|---|---|
| **Linguagem** | **Java** | `25 (LTS)` | Uso de Java Records para DTOs imutáveis e Pattern Matching. |
| **Framework** | **Spring Boot** | `3.4.3` | Versão moderna do framework com Spring Web e Spring Data JPA. |
| **Banco de Dados** | **MySQL** | `8.4 LTS` | SGBD relacional com charset `utf8mb4` e collation `utf8mb4_unicode_ci`. |
| **Mapeamento JPA** | **Hibernate** | `6.6.8` | Mapeamento ORM com `AttributeConverters` para enums acentuados. |
| **Validação** | **Bean Validation** | `3.1.0` | Validação declarativa via anotações (`@NotBlank`, `@Size`, `@NotNull`). |
| **Documentação** | **Springdoc OpenAPI** | `2.8.5` | Geração automática da especificação OpenAPI 3 e Swagger UI. |
| **Driver MySQL** | **MySQL Connector/J**| `9.x` | Driver de conexão JDBC otimizado com suporte a UTF-8. |
| **Testes Unitários**| **JUnit 5 & Mockito** | `5.11` | Suíte completa para testes de isolamento de serviço e controlador. |
| **Banco de Testes** | **H2 Database** | `2.3.x` | Banco em memória em modo MySQL para testes de integração rápidos. |

---

## 📋 Regras de Negócio e Ciclo de Vida da Tarefa

1. **Valores Padrão**:
   - Status inicial: `A FAZER`
   - Prioridade inicial: `MÉDIA`
2. **Ciclo de Vida do `completedAt`**:
   - Quando o status é alterado para `CONCLUÍDA`, o sistema preenche `completedAt` com o timestamp atual (`LocalDateTime.now()`).
   - Se a tarefa sair do status `CONCLUÍDA` para qualquer outro (`A FAZER`, `FAZENDO`, `ARCHIVADA`), `completedAt` é automaticamente redefinido para `null`.
3. **Filtro Padrão de Arquivadas**:
   - Tarefas com status `ARCHIVADA` **não são retornadas** na listagem principal por padrão, a menos que o filtro de status seja solicitado explicitamente como `status=ARCHIVADA`.
4. **Proteção contra Sobrecarga de Paginação**:
   - Tamanho padrão da página: `10`
   - Tamanho máximo permitido: `100` (solicitações com `size > 100` são ajustadas para `100`).
5. **Datas e Formatação**:
   - `dueDate`: Recebido e retornado no formato `DD-MM-YYYY` (`LocalDate`).
   - `createdAt`, `updatedAt`, `completedAt`: Formato ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`).

---

## 🌐 Especificação do Contrato da API REST

**Prefix da API**: `/api/v1`

### Tabela de Endpoints

| Método | Endpoint | Descrição | Status Sucesso | Status Erro |
|---|---|---|---|---|
| `GET` | `/api/v1/tasks` | Lista tarefas com busca, filtros, ordenação e paginação | `200 OK` | `400 Bad Request` |
| `POST` | `/api/v1/tasks` | Criar nova tarefa | `201 Created` | `400 Bad Request` |
| `GET` | `/api/v1/tasks/{id}` | Buscar tarefa por ID | `200 OK` | `404 Not Found` |
| `PUT` | `/api/v1/tasks/{id}` | Atualizar tarefa completa por ID | `200 OK` | `400 Bad Request`, `404 Not Found` |
| `PATCH` | `/api/v1/tasks/{id}/status` | Alterar apenas o status da tarefa | `200 OK` | `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/v1/tasks/{id}` | Excluir tarefa fisicamente | `204 No Content` | `404 Not Found` |

---

### Parâmetros da Consulta `GET /api/v1/tasks`

- `search` *(opcional)*: Pesquisa textual por título ou descrição (case-insensitive).
- `status` *(opcional)*: Enum (`A FAZER`, `FAZENDO`, `CONCLUÍDA`, `ARCHIVADA`).
- `priority` *(opcional)*: Enum (`BAIXA`, `MÉDIA`, `ALTA`).
- `dueDateFrom` *(opcional)*: Data inicial de vencimento (`DD-MM-YYYY`).
- `dueDateTo` *(opcional)*: Data final de vencimento (`DD-MM-YYYY`).
- `page` *(opcional, padrão `0`)*: Número da página (0-indexed).
- `size` *(opcional, padrão `10`, máximo `100`)*: Tamanho da página.
- `sortBy` *(opcional, padrão `createdAt`)*: Campo para ordenação (`createdAt`, `updatedAt`, `dueDate`, `title`, `priority`, `status`).
- `sortDir` *(opcional, padrão `DESC`)*: Direção (`ASC` ou `DESC`).

---

### Exemplo de Resposta de Sucesso (`GET /api/v1/tasks`)

```json
{
  "content": [
    {
      "id": 1,
      "title": "Estudar Spring Boot 3.5 e Java 25",
      "description": "Revisar recursos modernos como Records, Pattern Matching e especificações JPA",
      "status": "FAZENDO",
      "priority": "ALTA",
      "dueDate": "25-09-2026",
      "completedAt": null,
      "createdAt": "2026-09-23T15:00:00",
      "updatedAt": "2026-09-23T15:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "sort": "createdAt: DESC"
}
```

---

### Formatador Padronizado de Erros

Todas as exceções tratadas pelo `@RestControllerAdvice` retornam uma estrutura de erro uniforme:

```json
{
  "timestamp": "2026-09-25T13:30:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Existem campos inválidos.",
  "path": "/api/v1/tasks",
  "fieldErrors": [
    {
      "field": "title",
      "message": "O título é obrigatório."
    }
  ]
}
```

---

## ⚡ Exemplos Práticos de Chamadas cURL

### 1. Criar Tarefa (`POST`)

```bash
curl -X POST "http://localhost:8080/api/v1/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Revisar Arquitetura do Sistema",
    "description": "Verificar separação de DTOs e mapeadores manuais",
    "status": "A FAZER",
    "priority": "ALTA",
    "dueDate": "30-09-2026"
  }'
```

### 2. Listar com Busca e Filtros (`GET`)

```bash
curl -X GET "http://localhost:8080/api/v1/tasks?search=Arquitetura&priority=ALTA&page=0&size=10"
```

### 3. Alterar Apenas o Status (`PATCH`)

```bash
curl -X PATCH "http://localhost:8080/api/v1/tasks/1/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONCLUÍDA"
  }'
```

### 4. Excluir Tarefa (`DELETE`)

```bash
curl -X DELETE "http://localhost:8080/api/v1/tasks/1"
```

---

## 📚 Documentação Swagger UI / OpenAPI

Com a aplicação em execução, acesse a documentação interativa no navegador:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec**: `http://localhost:8080/v3/api-docs`

---

## 🔒 Estratégia de Segurança para API Pública

1. **Validação Estrita no Servidor**: Uso de Bean Validation em todas as entradas de requisição.
2. **Prevenção contra SQL Injection**: Consultas dinâmicas construídas exclusivamente via `JpaSpecificationExecutor` do Spring Data JPA com parâmetros tipados.
3. **Configuração Restritiva de CORS**: Origens permitidas restringidas ao endereço `http://localhost:5173` no ambiente de desenvolvimento (configurável via `CORS_ALLOWED_ORIGIN`).
4. **Sanitização de Exceções**: O `@RestControllerAdvice` esconde qualquer detalhe interno do Hibernate, SQL ou JVM, retornando apenas mensagens sanitizadas ao cliente.

---

## 🚀 Como Executar o Back-End

### Pré-requisitos
- **Java JDK**: `25` (LTS) ou superior
- **MySQL Server**: `8.0` ou `8.4 LTS` rodando na porta `3306`

### 1. Inicializar o Banco de Dados MySQL

Execute o script de criação e dados no MySQL Workbench ou CLI:

```bash
mysql -u root -p < ../database/schema.sql
mysql -u root -p < ../database/data.sql
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na pasta `backend` a partir do `.env.example`:

```bash
# Windows (PowerShell)
$env:DB_URL="jdbc:mysql://localhost:3306/todolist_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useUnicode=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="sua_senha_mysql"
$env:CORS_ALLOWED_ORIGIN="http://localhost:5173"
```

### 3. Compilar e Executar a Aplicação

```bash
# Windows (usando o Maven Wrapper incluído)
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API estará rodando em **`http://localhost:8080`**.

---

## 🧪 Executando os Testes Automatizados

Para rodar a suíte completa de testes unitários e de integração (15 testes):

```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

Os testes incluem:
- Unitários do `TaskService` (regras do `completedAt`, limites de paginação).
- Unitários do `TaskMapper`.
- Testes dos endpoints do controlador `TaskController` com `MockMvc`.
- Teste de integração de repositório `TaskRepositoryIntegrationTest` com H2 em memória.

---

## 📄 Licença

Este componente faz parte da solução full-stack TaskManager e está licenciado sob a licença MIT.
