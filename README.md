# 🛒 API de Gerenciamento de Produtos e E-commerce

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Arquitetura e Tecnologias](#-arquitetura-e-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Executando o Projeto](#-executando-o-projeto)
- [Documentação da API](#-documentação-da-api)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Modelos de Dados](#-modelos-de-dados)
- [Autenticação e Autorização](#-autenticação-e-autorização)
- [Endpoints Principais](#-endpoints-principais)
- [Testes](#-testes)
- [Versionamento de Banco de Dados](#-versionamento-de-banco-de-dados)
- [Auditoria](#-auditoria)
- [Tratamento de Erros](#-tratamento-de-erros)
- [Boas Práticas](#-boas-práticas)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)
- [Contato](#-contato)

## 🎯 Sobre o Projeto

A **API de Gerenciamento de Produtos e E-commerce** é uma solução completa e robusta para gerenciamento de produtos, estoque, categorias, pedidos e carrinho de compras. Desenvolvida com Spring Boot 3.5.7 e Java 21, a aplicação implementa as melhores práticas de desenvolvimento, incluindo segurança com JWT, auditoria completa de operações e documentação interativa com Swagger.

### ✨ Diferenciais

- 🔐 **Segurança Robusta**: Autenticação JWT com controle de acesso baseado em roles (ADMIN, VENDEDOR, CLIENTE)
- 📊 **Auditoria Completa**: Registro detalhado de todas as operações críticas do sistema
- 🔄 **Controle de Estoque**: Gerenciamento avançado com histórico de movimentações (entrada/saída)
- 🛒 **Carrinho de Compras**: Sistema completo de carrinho com validações de estoque e preços
- 📦 **Gestão de Pedidos**: Fluxo completo de pedidos com controle de status
- 🏷️ **Categorias Hierárquicas**: Sistema de categorias com suporte a hierarquia pai-filho
- 📚 **Documentação Interativa**: Swagger UI totalmente configurado com exemplos
- 🧪 **Cobertura de Testes**: Testes unitários e de integração
- 🗄️ **Versionamento de Schema**: Flyway para controle de migrações de banco de dados

## 🏗️ Arquitetura e Tecnologias

### Core Framework
- **Spring Boot 3.5.7**: Framework principal
- **Java 21**: Linguagem de programação com recursos modernos
- **Maven**: Gerenciamento de dependências e build

### Camada de Persistência
- **Spring Data JPA**: Abstração de acesso a dados
- **Hibernate**: ORM (Object-Relational Mapping)
- **H2 Database**: Banco de dados em memória para desenvolvimento
- **Flyway**: Controle de versão de banco de dados

### Segurança
- **Spring Security**: Framework de segurança
- **JWT (JSON Web Token)**: Autenticação stateless
- **BCrypt**: Hash de senhas

### Documentação
- **SpringDoc OpenAPI 3**: Geração automática de documentação
- **Swagger UI**: Interface interativa para testes de API

### Validação e Mapeamento
- **Bean Validation**: Validação de dados com anotações
- **Lombok**: Redução de código boilerplate
- **MapStruct Pattern**: Mapeamento entre DTOs e entidades

### Testes
- **JUnit 5**: Framework de testes
- **Mockito**: Mock de dependências
- **Spring Boot Test**: Testes de integração

## 🚀 Funcionalidades

### 👤 Gestão de Usuários
- ✅ Cadastro de usuários com validação de e-mail e senha forte
- ✅ Autenticação via JWT
- ✅ Controle de acesso baseado em roles (ADMIN, VENDEDOR, CLIENTE)
- ✅ Atualização de perfil
- ✅ Gestão de papéis/roles por usuário

### 📦 Gestão de Produtos
- ✅ CRUD completo de produtos
- ✅ Associação obrigatória com categoria
- ✅ Status ativo/inativo

### 🏷️ Gestão de Categorias
- ✅ CRUD de categorias
- ✅ Suporte a hierarquia (categorias pai-filho)
- ✅ Validação de nome único por nível
- ✅ Timestamps de criação e atualização

### 📊 Controle de Estoque
- ✅ Gerenciamento de estoque por produto
- ✅ Registro de movimentações (ENTRADA/SAÍDA)
- ✅ Histórico completo de movimentações
- ✅ Validação de estoque disponível
- ✅ Prevenção de estoque negativo

### 🛒 Carrinho de Compras
- ✅ Um carrinho ativo por usuário
- ✅ Adicionar/remover produtos
- ✅ Atualizar quantidade de itens
- ✅ Captura de preço no momento da adição (priceSnapshot)
- ✅ Validação de estoque disponível
- ✅ Cancelamento de carrinho com devolução ao estoque
- ✅ Finalização de carrinho gerando pedido

### 📝 Gestão de Pedidos
- ✅ Criação de pedidos a partir do carrinho
- ✅ Controle de status (NOVO, CONFIRMADO, ENVIADO, ENTREGUE, CANCELADO)
- ✅ Histórico de itens do pedido
- ✅ Cálculo automático de totais
- ✅ Validação de estoque antes da finalização

### 📋 Auditoria
- ✅ Registro de todas as operações críticas
- ✅ Rastreamento de usuário, entidade e operação
- ✅ Armazenamento de dados antes e depois da operação
- ✅ Consulta de logs de auditoria por entidade

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

```bash
# Java Development Kit (JDK) 21
java -version
# Deve retornar: java version "21.x.x"

# Maven 3.8+
mvn -version
# Deve retornar: Apache Maven 3.8.x ou superior

# Git (para clonar o repositório)
git --version
```

### Ferramentas Recomendadas
- **IDE**: IntelliJ IDEA, Eclipse ou VS Code
- **Cliente HTTP**: Postman, Insomnia ou Thunder Client
- **Navegador**: Para acessar Swagger UI e H2 Console

## 🔧 Instalação e Configuração

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/ApiProdutos.git
cd ApiProdutos
```

### 2. Configuração de Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis de ambiente:

```bash
# Chave secreta para JWT (OBRIGATÓRIO em produção)
JWT_SECRET=sua-chave-secreta-super-segura-aqui

# Tempo de expiração do token em milissegundos (padrão: 2 horas)
JWT_EXPIRATION=7200000
```

### 3. Configuração do Banco de Dados

#### Desenvolvimento (H2 - já configurado)
A aplicação usa H2 em memória por padrão. Nenhuma configuração adicional é necessária.

#### Produção (PostgreSQL, MySQL, etc.)
Edite `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/produtosdb
    username: seu_usuario
    password: sua_senha
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### 4. Instalar Dependências

```bash
mvn clean install
```

## 🚀 Executando o Projeto

### Modo Desenvolvimento

```bash
# Usando Maven
mvn spring-boot:run

# Ou compilando e executando
mvn clean package
java -jar target/AppProdutos-0.0.1-SNAPSHOT.jar
```

### Modo Produção

```bash
# Compilar para produção
mvn clean package -DskipTests

# Executar com perfil de produção
java -jar target/AppProdutos-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Verificar Aplicação

Após iniciar, você verá no console:

```
INFO : Started AppProdutosApplication in X.XXX seconds
INFO : Swagger UI disponível em: http://localhost:8080/swagger-ui.html
```

Acesse:
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **API Docs JSON**: http://localhost:8080/v3/api-docs

## 📚 Documentação da API

### Swagger UI

A documentação interativa está disponível em:
```
http://localhost:8080/swagger-ui.html
```

O Swagger UI permite:
- 📖 Visualizar todos os endpoints disponíveis
- 🧪 Testar endpoints diretamente no navegador
- 📝 Ver exemplos de request e response
- 🔐 Autenticar e testar endpoints protegidos

### Autenticação no Swagger

1. Faça login em `/auth/login` para obter o token
2. Clique no botão **"Authorize"** (cadeado) no topo da página
3. Insira o token no formato: `Bearer seu-token-jwt-aqui`
4. Clique em **"Authorize"** e depois **"Close"**

Agora você pode testar endpoints protegidos!

### Resumo Rápido dos Endpoints

| Recurso | Método | Endpoint | Auth | Descrição |
|---------|--------|----------|------|-----------|
| **Autenticação** | POST | `/auth/login` | Público | Login e geração de token JWT |
| | POST | `/auth/refresh` | Público | Renovar token JWT |
| | GET | `/auth/me` | Qualquer | Dados do usuário logado |
| **Produtos** | GET | `/v1/produtos` | ADMIN, VENDEDOR, CLIENTE | Listar produtos ativos |
| | GET | `/v1/produtos/{id}` | ADMIN, VENDEDOR, CLIENTE | Buscar produto por ID |
| | POST | `/v1/produtos` | ADMIN, VENDEDOR | Criar produto com estoque inicial |
| | PUT | `/v1/produtos/{id}` | ADMIN, VENDEDOR | Atualizar produto |
| | DELETE | `/v1/produtos/{id}` | ADMIN | Deletar produto (soft delete) |
| **Categorias** | GET | `/v1/categorias` | ADMIN, VENDEDOR, CLIENTE | Listar categorias |
| | GET | `/v1/categorias/{id}` | ADMIN, VENDEDOR, CLIENTE | Buscar categoria por ID |
| | POST | `/v1/categorias` | ADMIN | Criar categoria |
| | PUT | `/v1/categorias/{id}` | ADMIN | Atualizar categoria |
| | DELETE | `/v1/categorias/{id}` | ADMIN | Deletar categoria |
| **Estoque** | GET | `/v1/estoque/{produtoId}` | Qualquer | Consultar estoque |
| **Movimentos** | POST | `/v1/movimentos/entrada` | ADMIN, VENDEDOR | Registrar entrada |
| | POST | `/v1/movimentos/saida` | ADMIN, VENDEDOR | Registrar saída |
| | GET | `/v1/movimentos/{produtoId}` | ADMIN, VENDEDOR | Histórico de movimentações |
| **Carrinho** | GET | `/v1/carrinhos?usuarioId=X` | CLIENTE | Ver carrinho ativo |
| | POST | `/v1/carrinhos/produtos?usuarioId=X` | CLIENTE | Adicionar produto |
| | PUT | `/v1/carrinhos/produtos/{produtoId}?usuarioId=X` | CLIENTE | Atualizar quantidade |
| | DELETE | `/v1/carrinhos/produtos/{produtoId}?usuarioId=X` | CLIENTE | Remover produto |
| | POST | `/v1/carrinhos/finalizar?usuarioId=X` | CLIENTE | Finalizar (criar pedido) |
| | DELETE | `/v1/carrinhos/cancelar?usuarioId=X` | CLIENTE | Cancelar carrinho |
| **Pedidos** | GET | `/v1/pedidos?usuarioId=X` | CLIENTE, ADMIN | Listar pedidos |
| | GET | `/v1/pedidos/{id}` | CLIENTE, ADMIN | Buscar pedido por ID |
| | POST | `/v1/pedidos` | CLIENTE | Criar pedido |
| | PUT | `/v1/pedidos/{id}/status` | ADMIN | Atualizar status |
| **Auditoria** | GET | `/auditoria` | ADMIN | Listar logs de auditoria |
| | GET | `/auditoria?entidade=X` | ADMIN | Filtrar logs por entidade |

## 📁 Estrutura do Projeto

```
ApiProdutos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/grupo3/AppProdutos/
│   │   │       ├── auditoria/          # Sistema de auditoria
│   │   │       │   ├── AuditController.java
│   │   │       │   ├── AuditLog.java
│   │   │       │   ├── AuditLogRepository.java
│   │   │       │   ├── AuditService.java
│   │   │       │   └── TipoOperacao.java
│   │   │       ├── config/             # Configurações
│   │   │       │   ├── security/       # Segurança e JWT
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   └── SecurityFilter.java
│   │   │       │   └── swagger/        # Configuração Swagger
│   │   │       │       └── SwaggerConfig.java
│   │   │       ├── controller/         # Controladores REST
│   │   │       │   ├── AuthenticationController.java
│   │   │       │   ├── CarrinhoController.java
│   │   │       │   ├── CategoriaController.java
│   │   │       │   ├── EstoqueController.java
│   │   │       │   ├── MovimentoEstoqueController.java
│   │   │       │   ├── PedidoController.java
│   │   │       │   └── ProdutoController.java
│   │   │       ├── dto/                # Data Transfer Objects
│   │   │       │   ├── AutenticacaoDTO/
│   │   │       │   ├── CarrinhoDTO/
│   │   │       │   ├── CategoriaDTO/
│   │   │       │   ├── EstoqueDTO/
│   │   │       │   ├── PedidoDTO/
│   │   │       │   ├── ProdutoDTO/
│   │   │       │   └── UsuarioDTO/
│   │   │       ├── exception/          # Exceções personalizadas
│   │   │       │   ├── CamposPersonalizadosException.java
│   │   │       │   ├── CarrinhoNaoEncontradoException.java
│   │   │       │   ├── CategoriaNaoEncontradaException.java
│   │   │       │   ├── EstoqueInsuficienteException.java
│   │   │       │   ├── ProdutoJaExisteException.java
│   │   │       │   └── ...
│   │   │       ├── handler/            # Tratamento global de erros
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── mapper/             # Mapeadores DTO <-> Entity
│   │   │       │   ├── CarrinhoMapper.java
│   │   │       │   ├── PedidoMapper.java
│   │   │       │   ├── ProdutoMapper.java
│   │   │       │   └── UsuarioMapper.java
│   │   │       ├── model/              # Entidades JPA
│   │   │       │   ├── enums/
│   │   │       │   │   ├── Role.java
│   │   │       │   │   ├── StatusCarrinho.java
│   │   │       │   │   ├── StatusPedido.java
│   │   │       │   │   └── TipoMovimento.java
│   │   │       │   ├── Carrinho.java
│   │   │       │   ├── Categoria.java
│   │   │       │   ├── Estoque.java
│   │   │       │   ├── ItemCarrinho.java
│   │   │       │   ├── ItemPedido.java
│   │   │       │   ├── MovimentoEstoque.java
│   │   │       │   ├── Pedido.java
│   │   │       │   ├── Produto.java
│   │   │       │   └── Usuario.java
│   │   │       ├── repository/         # Repositórios JPA
│   │   │       │   ├── CarrinhoRepository.java
│   │   │       │   ├── CategoriaRepository.java
│   │   │       │   ├── EstoqueRepository.java
│   │   │       │   ├── ItemCarrinhoRepository.java
│   │   │       │   ├── MovimentoEstoqueRepository.java
│   │   │       │   ├── PedidoRepository.java
│   │   │       │   ├── ProdutoRepository.java
│   │   │       │   └── UsuarioRepository.java
│   │   │       ├── service/            # Lógica de negócio
│   │   │       │   ├── AuthenticationService.java
│   │   │       │   ├── AuthorizationService.java
│   │   │       │   ├── CarrinhoService.java
│   │   │       │   ├── CategoriaService.java
│   │   │       │   ├── EstoqueService.java
│   │   │       │   ├── MovimentoEstoqueService.java
│   │   │       │   ├── PedidoService.java
│   │   │       │   ├── ProdutoService.java
│   │   │       │   ├── TokenService.java
│   │   │       │   └── UsuarioService.java
│   │   │       └── AppProdutosApplication.java
│   │   └── resources/
│   │       ├── db/migration/           # Scripts Flyway
│   │       │   ├── V1__criar_tabela_produto.sql
│   │       │   ├── V2__criar_tabela_estoque.sql
│   │       │   ├── V3__criar_tabela_estoque_movimento.sql
│   │       │   ├── V4__criar_tabela_categoria.sql
│   │       │   ├── V5__criar_tabela_usuario.sql
│   │       │   ├── V6__criar_tabela_pedido.sql
│   │       │   ├── V7__criar_tabela_item_pedido.sql
│   │       │   ├── V8__criar_tabela_carrinhos.sql
│   │       │   ├── V9__criar_tabela_carrinho_itens.sql
│   │       │   ├── V10__criar_tabela_usuario_roles.sql
│   │       │   └── V11__criar_tabela_audit_log.sql
│   │       │   └── V12__insert_categorias_produtos.sql
│   │       └── application.yaml
│   └── test/
│       └── java/
│           └── com/grupo3/AppProdutos/
│               ├── repository/          # Testes de repositório
│               ├── service/             # Testes de serviço
│               └── AppProdutosApplicationTests.java
├── target/
├── .gitignore
├── pom.xml
└── README.md
```

## 🗂️ Modelos de Dados

### Diagrama de Relacionamentos

```
┌─────────────┐
│   Usuario   │
│─────────────│
│ id (PK)     │
│ nome        │
│ email       │
│ senha       │
│ roles []    │
└──────┬──────┘
       │
       │ 1:1
       ▼
┌─────────────┐      1:N      ┌──────────────┐
│  Carrinho   │◄──────────────│ ItemCarrinho │
│─────────────│               │──────────────│
│ id (PK)     │               │ id (PK)      │
│ usuarioId   │               │ carrinhoId   │
│ status      │               │ produtoId    │
└─────────────┘               │ quantidade   │
       │                      │ priceSnapshot│
       │ 1:N                  └──────────────┘
       │                             │
       ▼                             │ N:1
┌─────────────┐                     │
│   Pedido    │                     │
│─────────────│                     │
│ id (PK)     │                     │
│ usuarioId   │                     │
│ status      │                     ▼
│ total       │              ┌──────────────┐
└──────┬──────┘              │   Produto    │
       │                     │──────────────│
       │ 1:N                 │ id (PK)      │
       ▼                     │ nome         │
┌──────────────┐             │ sku          │
│  ItemPedido  │────N:1─────►│ preco        │
│──────────────│             │ categoriaId  │
│ id (PK)      │             └──────┬───────┘
│ pedidoId     │                    │
│ produtoId    │                    │ 1:1
│ quantidade   │                    ▼
│ precoUnitario│             ┌──────────────┐
└──────────────┘             │   Estoque    │
                             │──────────────│
┌──────────────┐             │ id (PK)      │
│  Categoria   │◄────N:1─────│ produtoId    │
│──────────────│             │ quantidade   │
│ id (PK)      │             └──────┬───────┘
│ nome         │                    │
│ parentId     │                    │ 1:N
└──────────────┘                    ▼
                             ┌──────────────────┐
                             │ MovimentoEstoque │
                             │──────────────────│
                             │ id (PK)          │
                             │ estoqueId        │
                             │ tipo             │
                             │ quantidade       │
                             │ dataMovimento    │
                             └──────────────────┘
```

### Entidades Principais

#### Usuario
```java
{
  "id": Long,
  "nome": String,
  "email": String (unique),
  "senha": String (hash),
  "roles": ["ADMIN", "VENDEDOR", "CLIENTE"],
  "ativo": Boolean,
  "criadoEm": LocalDateTime,
  "atualizadoEm": LocalDateTime
}
```

#### Produto
```java
{
  "id": Long,
  "nome": String,
  "sku": String (unique),
  "descricao": String,
  "preco": BigDecimal,
  "categoriaId": Long,
  "ativo": Boolean
}
```

#### Categoria
```java
{
  "id": Long,
  "nome": String,
  "parentId": Long (nullable),
  "criadoEm": LocalDateTime,
  "atualizadoEm": LocalDateTime
}
```

#### Carrinho
```java
{
  "id": Long,
  "usuarioId": Long,
  "status": "ABERTO" | "FINALIZADO" | "CANCELADO",
  "itens": [
    {
      "itemId": Long,
      "produtoId": Long,
      "nomeProduto": String,
      "quantidade": Integer,
      "priceSnapshot": BigDecimal,
      "subtotal": BigDecimal
    }
  ],
  "total": BigDecimal
}
```

#### Pedido
```java
{
  "id": Long,
  "usuarioId": Long,
  "status": "NOVO" | "CONFIRMADO" | "ENVIADO" | "ENTREGUE" | "CANCELADO",
  "total": BigDecimal,
  "criadoEm": LocalDateTime,
  "itens": [
    {
      "produtoId": Long,
      "quantidade": Integer,
      "precoUnitario": BigDecimal,
      "subtotal": BigDecimal
    }
  ]
}
```

## 🔐 Autenticação e Autorização

### Sistema de Roles

A aplicação implementa três níveis de acesso:

| Role | Descrição | Permissões |
|------|-----------|------------|
| **CLIENTE** | Usuário padrão | - Visualizar produtos e categorias<br>- Gerenciar próprio carrinho<br>- Criar e visualizar próprios pedidos |
| **VENDEDOR** | Vendedor/Operador | - Todas de CLIENTE<br>- Criar e editar produtos próprios<br>- Registrar movimentações de estoque<br>- Visualizar histórico de estoque |
| **ADMIN** | Administrador | - Todas as permissões<br>- Gerenciar usuários<br>- Gerenciar categorias<br>- Acesso completo a produtos<br>- Visualizar auditoria |

### Fluxo de Autenticação

```
1. Login
   POST /auth/login
   Body: { "login": "email", "senha" }
   ↓
   Retorna JWT Token + dados do usuário

2. Acesso a Recursos Protegidos
   Header: Authorization: Bearer {token}
   ↓
   Token validado e permissões verificadas
   ↓
   Acesso permitido ou negado (403)
```

### Exemplo de Uso

```bash
# 1. Fazer login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin@example.com",
    "senha": "Admin@123"
  }'

# Resposta:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "Admin",
  "email": "admin@example.com",
  "papeis": ["ADMIN"]
}

# 2. Usar token em requisições protegidas
curl -X GET http://localhost:8080/v1/produtos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."

# 3. Criar produto com estoque inicial (ADMIN ou VENDEDOR)
curl -X POST http://localhost:8080/v1/produtos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  -d '{
    "produto": {
      "nome": "Mouse Gamer",
      "sku": "MGM-001",
      "descricao": "Mouse gamer RGB 16000 DPI",
      "preco": 199.90,
      "precoCusto": 120.00,
      "categoriaId": 1,
      "ativo": true
    },
    "quantidadeInicial": 100
  }'

# 4. Adicionar produto ao carrinho (CLIENTE)
curl -X POST "http://localhost:8080/v1/carrinhos/produtos?usuarioId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  -d '{
    "produtoId": 1,
    "quantidade": 2
  }'

# 5. Finalizar carrinho (criar pedido)
curl -X POST "http://localhost:8080/v1/carrinhos/finalizar?usuarioId=1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

### Validação de Senha

As senhas devem atender aos seguintes critérios:
- ✅ Mínimo 8 caracteres
- ✅ Pelo menos 1 letra maiúscula
- ✅ Pelo menos 1 letra minúscula
- ✅ Pelo menos 1 número
- ✅ Pelo menos 1 caractere especial (@, #, $, %, etc.)

**Exemplo válido**: `Senha@123`

## 🔗 Endpoints Principais

### 🔐 Autenticação

#### POST `/auth/login`
Autenticar usuário e obter token JWT

**Request:**
```json
{
  "login": "admin@example.com",
  "senha": "Admin@123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "Admin",
  "email": "admin@example.com",
  "papeis": ["ADMIN"]
}
```

#### POST `/auth/refresh`
Renovar token JWT

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200):**
```json
{
  "token": "novo-token-jwt...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "Admin",
  "email": "admin@example.com",
  "papeis": ["ADMIN"]
}
```

#### GET `/auth/me`
Obter dados do usuário autenticado
- **Auth**: Requerido (qualquer role)
- **Response (200)**: Dados do usuário logado

### 📦 Produtos

#### GET `/v1/produtos`
Listar todos os produtos ativos
- **Auth**: ADMIN, VENDEDOR, CLIENTE
- **Response (200)**: Array de produtos

#### GET `/v1/produtos/{id}`
Buscar produto por ID
- **Auth**: ADMIN, VENDEDOR, CLIENTE
- **Response (200)**: Dados do produto

#### POST `/v1/produtos`
Criar novo produto com estoque inicial
- **Auth**: ADMIN, VENDEDOR

**Request:**
```json
{
  "produto": {
    "nome": "Mouse Gamer",
    "sku": "MGM-001",
    "descricao": "Mouse gamer RGB 16000 DPI",
    "preco": 199.90,
    "precoCusto": 120.00,
    "categoriaId": 1,
    "ativo": true
  },
  "quantidadeInicial": 100
}
```

**Response (201):**
```json
{
  "id": 1,
  "nome": "Mouse Gamer",
  "sku": "MGM-001",
  "descricao": "Mouse gamer RGB 16000 DPI",
  "preco": 199.90,
  "precoCusto": 120.00,
  "categoriaId": 1,
  "ativo": true
}
```

#### PUT `/v1/produtos/{id}`
Atualizar produto
- **Auth**: ADMIN, VENDEDOR

**Request:**
```json
{
  "nome": "Mouse Gamer Pro",
  "sku": "MGM-002",
  "descricao": "Mouse gamer RGB 16000 DPI Pro",
  "preco": 249.90,
  "precoCusto": 150.00,
  "categoriaId": 1
}
```

#### DELETE `/v1/produtos/{id}`
Deletar produto (soft delete - marca como inativo)
- **Auth**: ADMIN apenas
- **Response (204)**: Sem conteúdo

### 🏷️ Categorias

#### GET `/v1/categorias`
Listar todas as categorias
- **Auth**: ADMIN, VENDEDOR, CLIENTE
- **Response (200)**: Array de categorias

#### GET `/v1/categorias/{id}`
Buscar categoria por ID
- **Auth**: ADMIN, VENDEDOR, CLIENTE
- **Response (200)**: Dados da categoria

#### POST `/v1/categorias`
Criar categoria
- **Auth**: ADMIN apenas

**Request:**
```json
{
  "nome": "Eletrônicos",
  "parentId": null
}
```

**Response (201):**
```json
{
  "id": 1,
  "nome": "Eletrônicos",
  "parentId": null,
  "criadoEm": "2025-11-22T14:30:00",
  "atualizadoEm": "2025-11-22T14:30:00"
}
```

#### PUT `/v1/categorias/{id}`
Atualizar categoria
- **Auth**: ADMIN apenas

**Request:**
```json
{
  "nome": "Eletrônicos e Informática",
  "parentId": null
}
```

#### DELETE `/v1/categorias/{id}`
Deletar categoria
- **Auth**: ADMIN apenas
- **Response (204)**: Sem conteúdo

### 📊 Estoque

#### GET `/v1/estoque/{produtoId}`
Consultar estoque de produto
- **Auth**: Autenticado (qualquer role)

**Response (200):**
```json
{
  "id": 1,
  "produtoId": 1,
  "quantidade": 150,
  "quantidadeReservada": 10,
  "quantidadeDisponivel": 140
}
```

### 📦 Movimentações de Estoque

#### POST `/v1/movimentos/entrada`
Registrar entrada de estoque
- **Auth**: ADMIN, VENDEDOR

**Request:**
```json
{
  "produtoId": 1,
  "quantidade": 50
}
```

**Response (201):**
```json
{
  "id": 1,
  "estoqueId": 1,
  "tipo": "ENTRADA",
  "quantidade": 50,
  "dataMovimento": "2025-11-22T14:30:00"
}
```

#### POST `/v1/movimentos/saida`
Registrar saída de estoque
- **Auth**: ADMIN, VENDEDOR

**Request:**
```json
{
  "produtoId": 1,
  "quantidade": 10
}
```

**Response (201):**
```json
{
  "id": 2,
  "estoqueId": 1,
  "tipo": "SAIDA",
  "quantidade": 10,
  "dataMovimento": "2025-11-22T14:35:00"
}
```

#### GET `/v1/movimentos/{produtoId}`
Histórico de movimentações do produto
- **Auth**: ADMIN, VENDEDOR
- **Response (200)**: Array de movimentações ordenadas por data

### 🛒 Carrinho

#### GET `/v1/carrinhos?usuarioId={usuarioId}`
Ver carrinho ativo do usuário
- **Auth**: CLIENTE apenas

**Response (200):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "status": "ABERTO",
  "itens": [
    {
      "itemId": 1,
      "produtoId": 1,
      "nomeProduto": "Mouse Gamer",
      "quantidade": 2,
      "priceSnapshot": 199.90,
      "subtotal": 399.80
    }
  ],
  "total": 399.80
}
```

#### POST `/v1/carrinhos/produtos?usuarioId={usuarioId}`
Adicionar produto ao carrinho
- **Auth**: CLIENTE apenas

**Request:**
```json
{
  "produtoId": 1,
  "quantidade": 2
}
```

**Response (201):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "status": "ABERTO",
  "itens": [
    {
      "itemId": 1,
      "produtoId": 1,
      "nomeProduto": "Mouse Gamer",
      "quantidade": 2,
      "priceSnapshot": 199.90,
      "subtotal": 399.80
    }
  ],
  "total": 399.80
}
```

#### PUT `/v1/carrinhos/produtos/{produtoId}?usuarioId={usuarioId}`
Atualizar quantidade de produto
- **Auth**: CLIENTE apenas

**Request:**
```json
{
  "quantidade": 5
}
```

#### DELETE `/v1/carrinhos/produtos/{produtoId}?usuarioId={usuarioId}`
Remover produto do carrinho
- **Auth**: CLIENTE apenas
- **Response (200)**: Carrinho atualizado

#### POST `/v1/carrinhos/finalizar?usuarioId={usuarioId}`
Finalizar carrinho (criar pedido)
- **Auth**: CLIENTE apenas

**Response (200):**
```json
{
  "pedidoId": 1,
  "message": "Carrinho finalizado e pedido criado com sucesso",
  "total": 399.80
}
```

#### DELETE `/v1/carrinhos/cancelar?usuarioId={usuarioId}`
Cancelar carrinho (devolver itens ao estoque)
- **Auth**: CLIENTE apenas
- **Response (200)**: Mensagem de confirmação

### 📝 Pedidos

#### GET `/v1/pedidos?usuarioId={usuarioId}`
Listar pedidos do usuário logado
- **Auth**: CLIENTE (próprios pedidos), ADMIN (todos)
- **Response (200)**: Array de pedidos

#### GET `/v1/pedidos/{id}`
Buscar pedido por ID
- **Auth**: CLIENTE (apenas próprios), ADMIN (todos)

**Response (200):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "status": "PENDENTE",
  "total": 399.80,
  "criadoEm": "2025-11-22T14:30:00",
  "itens": [
    {
      "id": 1,
      "produtoId": 1,
      "nomeProduto": "Mouse Gamer",
      "quantidade": 2,
      "precoUnitario": 199.90,
      "subtotal": 399.80
    }
  ]
}
```

#### POST `/v1/pedidos`
Criar novo pedido
- **Auth**: CLIENTE apenas

**Request:**
```json
{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ]
}
```

#### PUT `/v1/pedidos/{id}/status`
Atualizar status do pedido
- **Auth**: ADMIN apenas

**Request:**
```json
{
  "status": "CONFIRMADO"
}
```

**Status disponíveis**: `PENDENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGUE`, `CANCELADO`

### 📋 Auditoria

#### GET `/auditoria`
Buscar todos os logs de auditoria
- **Auth**: ADMIN
- **Response (200)**: Array completo de logs

#### GET `/auditoria?entidade={entidade}`
Buscar logs de uma entidade específica
- **Auth**: ADMIN
- **Exemplo**: `/auditoria?entidade=PRODUTO`
- **Response (200)**: Array de logs filtrados

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes Específicos

```bash
# Testes de serviço
mvn test -Dtest=ProdutoServiceTest

# Testes de repositório
mvn test -Dtest=ProdutoRepositoryTest
```

### Cobertura de Testes

```bash
mvn clean test jacoco:report
```

Relatório disponível em: `target/site/jacoco/index.html`

### Estrutura de Testes

```java
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    
    @Mock
    private ProdutoRepository produtoRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private ProdutoService produtoService;
    
    @Test
    void deveCriarProdutoComSucesso() {
        // Arrange
        ProdutoCreateDTO dto = new ProdutoCreateDTO(...);
        
        // Act
        ProdutoViewDTO resultado = produtoService.criarProduto(dto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Produto Teste", resultado.nome());
    }
}
```

## 🗄️ Versionamento de Banco de Dados

O projeto utiliza **Flyway** para controle de versão do banco de dados.

### Migrations

Localização: `src/main/resources/db/migration/`

| Versão | Arquivo                                  | Descrição                              |
|--------|------------------------------------------|----------------------------------------|
| V1 | `V1__criar_tabela_produto.sql`           | Criação da tabela de produtos          |
| V2 | `V2__criar_tabela_estoque.sql`           | Criação da tabela de estoque           |
| V3 | `V3__criar_tabela_estoque_movimento.sql` | Criação da tabela de movimentações     |
| V4 | `V4__criar_tabela_categoria.sql`         | Criação da tabela de categorias        |
| V5 | `V5__criar_tabela_usuario.sql`           | Criação da tabela de usuários          |
| V6 | `V6__criar_tabela_pedido.sql`            | Criação da tabela de pedidos           |
| V7 | `V7__criar_tabela_item_pedido.sql`       | Criação da tabela de itens do pedido   |
| V8 | `V8__criar_tabela_carrinhos.sql`         | Criação da tabela de carrinhos         |
| V9 | `V9__criar_tabela_carrinho_itens.sql`    | Criação da tabela de itens do carrinho |
| V10 | `V10__criar_tabela_usuario_roles.sql`    | Criação da tabela de roles             |
| V11 | `V11__criar_tabela_audit_log.sql`        | Criação da tabela de auditoria         |
| V11 | `V12__insert_categorias_produto.sql`     | Inserção de dados nas tabelas          |

### Criar Nova Migration

```bash
# Criar arquivo seguindo o padrão: V{versao}__{descricao}.sql
# Exemplo: V12__adicionar_coluna_desconto_produto.sql
```

**Exemplo de migration:**
```sql
-- V12__adicionar_coluna_desconto_produto.sql
ALTER TABLE tb_produto 
ADD COLUMN desconto DECIMAL(5,2) DEFAULT 0.00;
```

### Comandos Flyway

```bash
# Verificar status das migrations
mvn flyway:info

# Aplicar migrations pendentes
mvn flyway:migrate

# Limpar banco (CUIDADO em produção!)
mvn flyway:clean
```

## 📋 Auditoria

O sistema possui auditoria completa de operações críticas.

### Entidades Auditadas

- ✅ Usuários (criação, atualização, exclusão)
- ✅ Produtos (criação, atualização, exclusão)
- ✅ Categorias (criação, atualização, exclusão)
- ✅ Estoque (entrada, saída)
- ✅ Carrinho (finalização, cancelamento)
- ✅ Pedidos (criação, atualização de status)

### Estrutura do Log de Auditoria

```json
{
  "id": 1,
  "usuarioId": 1,
  "nomeUsuario": "João Silva",
  "entidade": "PRODUTO",
  "entidadeId": 10,
  "operacao": "UPDATE",
  "dadosAntigos": "{\"nome\":\"Mouse Antigo\",\"preco\":150.00}",
  "dadosNovos": "{\"nome\":\"Mouse Gamer\",\"preco\":199.90}",
  "dataHora": "2025-11-22T14:30:00",
}
```

### Consultar Logs de Auditoria

#### GET `/v1/auditoria/{entidade}`
Buscar logs de uma entidade específica
- **Auth**: ADMIN
- **Exemplo**: `/api/auditoria/Produto`

#### GET `/v1/auditoria`
Buscar todos os dados auditados.
- **Auth**: ADMIN

## ⚠️ Tratamento de Erros

### Estrutura de Erro Padrão

```json
{
  "timestamp": "2025-11-22T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Produto com este SKU já existe",
  "path": "/v1/produtos"
}
```

### Códigos de Status HTTP

| Código | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Requisição bem-sucedida |
| **201** | Created | Recurso criado com sucesso |
| **204** | No Content | Exclusão bem-sucedida |
| **400** | Bad Request | Dados inválidos na requisição |
| **401** | Unauthorized | Token ausente ou inválido |
| **403** | Forbidden | Sem permissão para o recurso |
| **404** | Not Found | Recurso não encontrado |
| **409** | Conflict | Conflito (ex: SKU duplicado, estoque insuficiente) |
| **500** | Internal Server Error | Erro interno do servidor |

### Exceções Personalizadas

- `ProdutoJaExisteException` - SKU ou código de barras duplicado
- `ProdutoNaoEncontradoException` - Produto não existe
- `CategoriaNaoEncontradaException` - Categoria não existe
- `EstoqueInsuficienteException` - Quantidade solicitada maior que disponível
- `CarrinhoNaoEncontradoException` - Carrinho não existe
- `PedidoNaoEncontradoException` - Pedido não existe
- `UsuarioNaoEncontradoException` - Usuário não existe
- `SenhaInvalidaException` - Senha não atende critérios de segurança

## ✅ Boas Práticas

### Implementadas no Projeto

1. **Arquitetura em Camadas**
   - Controller → Service → Repository
   - Separação clara de responsabilidades

2. **DTOs para Transferência de Dados**
   - `CreateDTO` - Criação de recursos
   - `UpdateDTO` - Atualização de recursos
   - `ViewDTO` - Visualização de dados
   - Nunca expor entidades diretamente

3. **Validação de Dados**
   - Bean Validation com anotações
   - Validações personalizadas quando necessário

4. **Segurança**
   - Senhas sempre com hash BCrypt
   - JWT para autenticação stateless
   - Controle de acesso baseado em roles
   - Proteção contra SQL Injection (JPA/Hibernate)

5. **Tratamento de Erros**
   - Global Exception Handler
   - Exceções personalizadas
   - Mensagens claras e padronizadas

6. **Documentação**
   - Swagger/OpenAPI completo
   - Comentários JavaDoc
   - README detalhado

7. **Versionamento**
   - Flyway para banco de dados
   - Git para código fonte

8. **Testes**
   - Testes unitários com Mockito
   - Testes de integração

9. **Auditoria**
   - Registro de operações críticas
   - Rastreabilidade completa

10. **Clean Code**
    - Nomes descritivos
    - Métodos pequenos e focados
    - Baixo acoplamento, alta coesão

## 🤝 Contribuindo

Contribuições são bem-vindas! Siga estas etapas:

### 1. Fork o Projeto

```bash
# Clone seu fork
git clone https://github.com/seu-usuario/ApiProdutos.git
cd ApiProdutos
```

### 2. Crie uma Branch

```bash
git checkout -b feature/nova-funcionalidade
```

### 3. Faça suas Alterações

- Escreva código limpo e comentado
- Adicione testes para novas funcionalidades
- Siga os padrões do projeto

### 4. Commit suas Mudanças

```bash
git add .
git commit -m "feat: adiciona nova funcionalidade X"
```

**Padrão de Commits:**
- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Documentação
- `refactor:` Refatoração
- `test:` Testes
- `chore:` Tarefas de manutenção

### 5. Push e Pull Request

```bash
git push origin feature/nova-funcionalidade
```

Abra um Pull Request no GitHub com:
- Descrição clara das mudanças
- Referência a issues relacionadas
- Screenshots (se aplicável)

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

```
MIT License

Copyright (c) 2025 Grupo 3

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
---

## 📊 Status do Projeto

✅ **Versão Atual**: 1.0.0  
🚀 **Status**: Produção  
📅 **Última Atualização**: Novembro 2025  
👥 **Mantenedores Ativos**: 3

---

## 🙏 Agradecimentos

- Spring Framework Team
- Comunidade Java
- Contribuidores do projeto
- Todos que testaram e reportaram issues

---

<div align="center">
  
### ⭐ Se este projeto foi útil, considere dar uma estrela no GitHub!

**Desenvolvido com ❤️ por Grupo 3**

</div>

