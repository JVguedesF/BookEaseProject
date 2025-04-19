# BookEase - Sistema de Gerenciamento de Consultas Clínicas

## Descrição
BookEase é um sistema de gerenciamento de consultas clínicas desenvolvido com Spring Boot. A aplicação permite o gerenciamento de clínicas, médicos e pacientes, facilitando o agendamento de consultas e procedimentos clínicos.

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.4.3
- Spring Security com OAuth2/JWT
- JPA/Hibernate
- PostgreSQL
- Maven

## Estrutura do Projeto
```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── bookease
│   │   │           ├── AutoConfig
│   │   │           ├── BookEaseApplication.java
│   │   │           ├── config
│   │   │           ├── controller
│   │   │           ├── exception
│   │   │           ├── model
│   │   │           ├── repository
│   │   │           └── service
│   │   └── resources
│   │       ├── app.key
│   │       ├── application.properties
│   │       ├── application-secret.properties
│   │       ├── app.pub
│   │       ├── static
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── bookease
│                   ├── BookEaseApplicationTests.java
│                   ├── repository
│                   └── service
```

O projeto segue uma arquitetura em camadas:
- **AutoConfig**: Configurações automáticas para inicialização do sistema
- **config**: Configurações de segurança, CORS e outras configurações da aplicação
- **controller**: Endpoints da API REST
- **exception**: Classes de exceções personalizadas
- **model**: Entidades, DTOs e mapeadores
- **repository**: Interfaces de acesso ao banco de dados
- **service**: Regras de negócio e lógica da aplicação

## Requisitos
- JDK 21
- Maven 3.8+
- PostgreSQL 12+

## Configuração

### Banco de Dados
O sistema utiliza PostgreSQL como banco de dados. Configure a conexão no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookease
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### Configurações de Segurança
A aplicação utiliza JWT para autenticação. Configure os caminhos das chaves no `application.properties`:

```properties
jwt.private.key=app.key
jwt.public.key=app.pub
```

### Dados Iniciais
A aplicação inicializa alguns dados por padrão no arquivo `application-secret.properties`.

## Compilação e Execução

### Compilar o Projeto
```bash
mvn clean package
```

### Executar a Aplicação
```bash
mvn spring-boot:run
```

Ou após compilar:
```bash
java -jar target/BookEase-0.0.1-SNAPSHOT.jar
```

## Principais Funcionalidades

### Autenticação
O sistema utiliza JWT para autenticação:
- Endpoint: `/auth/login`
- Método: POST
- Body: `{ "username": "seu_usuario", "password": "sua_senha" }`

### Usuários e Perfis
O sistema possui quatro tipos de perfis:
- **ADMIN**: Administrador do sistema
- **DOCTOR**: Médicos/especialistas
- **PATIENT**: Pacientes
- **CLINIC**: Clínicas

### Especialidades e Procedimentos
O sistema inicializa automaticamente com uma lista de especialidades médicas e procedimentos comuns.

### Segurança
- Autenticação baseada em JWT
- Autorização por roles
- Senhas criptografadas com BCrypt
- Possibilidade de revogação de tokens

## Observações
- Por padrão, a aplicação roda na porta 8080
- As configurações de CORS permitem requisições do localhost:3000

## Testes
Para executar os testes automatizados:
```bash
mvn test
```
O sistema utiliza H2 como banco de dados em memória para testes.

# 🚧 Frontend em Construção!  
**Sistema de agendamentos no "canteiro de obras" digital**  

🛠 **Status do projeto:**  
- Backend funcionando como relógio suíço ⚙️✅  
- Frontend recebendo "acabamentos finais" 👷🎨  
