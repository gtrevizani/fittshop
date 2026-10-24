# 🏋️ FittShop — E-Commerce de Alta Performance, Suplementos & Moda Fitness

> **Plataforma Omnichannel de E-Commerce para Atletas e Praticantes de Academia**  
> **Conduzida e Auditada sob o protocolo canônico do [Agent Master DevSecOps](https://github.com/ContalApps/agent-master-devsecops)**  
> **Stack Principal:** Java 21 LTS + Spring Boot 3.3.x (Clean Architecture) | Angular 18+ Standalone (Signals) | Docker

---

## ⚡ 1. Visão Geral do Projeto

A **FittShop** é uma loja virtual desenvolvida para atender às demandas de praticantes de musculação, bodybuilders, crossfitters e atletas de alta performance. Além de compras tradicionais, a plataforma conta com:

- 🧠 **Consultor NutriFit IA:** Motor de recomendação baseado no peso, altura, frequência semanal e objetivo esportivo (Hipertrofia, Definição, Powerlifting, Resistência) com cálculo diário de proteínas (g/kg), hidratação e montagem de combos com 15% de desconto.
- 🔒 **Reserva Atômica de Estoque com Lease TTL (15 min):** Trava atômica no banco de dados que impede *overselling* (vender produtos sem estoque em momentos de picos de promoção). Se o pagamento não for efetuado em 15 minutos, o saldo retorna à disponibilidade pública.
- 🪙 **Fidelidade FittCoins (Gamificação):** Cashback em moeda própria da loja (1 FittCoin para cada R$ 10,00 gastos, valendo R$ 0,50 no resgate).
- 🚚 **FittFreight Logistics:** Cálculo dinâmico de frete por CEP com isenção progressiva (Frete Grátis Sul/Sudeste > R$ 199,00).
- 🏢 **Central de Operações Integradas (Backoffice Omnichannel):** Baseado nas rotinas operacionais físicas:
  - **Estoquista:** Recebimento de fardos de fornecedores, registro de lote e validade sanitária ANVISA, alertas de estoque baixo e conferência para despacho.
  - **Caixa / PDV:** Conferência de pedidos, baixas manuais e conciliação de faturamento diário.
  - **Vendedora:** Registro de vendas assistidas com tracking de comissão de 4%.
  - **Gerente:** Dashboard executivo com ticket médio e balanço contábil.

---

## 🏛️ 2. Arquitetura & Documentação Canônica

Toda a engenharia e governança do projeto foram formalizadas na pasta `docs/` seguindo as diretrizes do **Agent Master DevSecOps**:

- 🧭 [`docs/SPEC_DISCOVERY.md`](./docs/SPEC_DISCOVERY.md): Elicitação de requisitos, Bounded Contexts, 30 Requisitos Funcionais (RF), 15 Requisitos Não-Funcionais (RNF), 25 Regras de Negócio (RN), formulações matemáticas dos algoritmos e matrizes BDD (Given/When/Then).
- 🏛️ [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md): Diagramas UML completos em Mermaid:
  - Diagrama de Casos de Uso (Atleta, Vendedora, Caixa, Estoquista, Gerente)
  - Diagrama de Componentes (Frontend Angular + Backend Spring Boot + Persistência)
  - Diagrama de Sequência (Checkout End-to-End, Reserva Atômica e Baixa de Estoque)
  - Diagrama de Sequência (Motor NutriFit de Recomendação)
  - Diagrama de Estados FSM do Pedido e Ciclo de Vida da Reserva
  - Diagrama Entidade-Relacionamento e Matriz DevSecOps (OWASP Top 10)

---

## 🛠️ 3. Estrutura do Repositório

```
fittshop/
├── backend/                         # Backend Java 21 + Spring Boot 3
│   ├── src/main/java/com/fittshop/
│   │   ├── domain/
│   │   │   ├── algorithms/          # 5 Algoritmos Canônicos de Negócio
│   │   │   ├── dto/                 # Contratos REST e Payloads
│   │   │   ├── enums/               # FSM States, PaymentMethod, RoleType
│   │   │   └── model/               # Entidades JPA (Product, Order, StockReservation, etc.)
│   │   ├── repository/              # Spring Data JPA Repositories
│   │   ├── service/                 # Serviços de Aplicação (Checkout, Inventory, etc.)
│   │   ├── controller/              # REST Controllers com OpenAPI Swagger
│   │   └── config/                  # SecurityConfig, DataInitializer (Seed Data)
│   ├── src/test/java/               # 9 Suítes de Testes Unitários de Algoritmos (100% Verdes)
│   ├── pom.xml                      # Dependências Maven
│   ├── mvnw & mvnw.cmd              # Maven Wrapper integrado
│   └── Dockerfile                   # Build multi-stage Java 21
│
├── frontend/                        # Frontend Angular 18+ Standalone
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── header/              # Barra de navegação e seletor de perfil operacional
│   │   │   ├── hero/                # Banner principal com seletor de objetivos de treino
│   │   │   ├── recommender/         # Wizard interativo NutriFit IA
│   │   │   ├── catalog/             # Grid de produtos com filtros e fichas técnicas
│   │   │   ├── cart-drawer/         # Sacola reativa com cupom, FittCoins e frete
│   │   │   ├── checkout-modal/      # Checkout transparente com PIX QR Code dinâmico
│   │   │   └── backoffice/          # Módulo de Estoquista, Caixa, Vendedoras e Gerência
│   │   ├── services/                # CartService (Signals), ProductService, AuthService
│   │   └── models/                  # Interfaces e Tipos TypeScript
│   ├── src/styles.css               # Design System Atlético Dark Neon
│   └── Dockerfile                   # Build multi-stage Nginx Alpine
│
├── docs/                            # Documentação canônica do Agent Master
│   ├── SPEC_DISCOVERY.md
│   └── ARCHITECTURE.md
│
├── docker-compose.yml               # Orquestrador local (Postgres + Backend + Frontend)
└── .github/workflows/ci.yml         # Pipeline CI/CD com SAST Gate
```

---

## 🚀 4. Como Executar o Projeto

### Pré-requisitos
- **Java 21 LTS**
- **Node.js 20+**
- **Docker & Docker Compose** (Opcional, para execução em containers)

### A. Executando o Backend (Spring Boot)
O backend possui o **Maven Wrapper** embutido, dispensando instalação prévia do Maven:

```bash
cd backend

# Executar suíte de testes unitários dos algoritmos de negócio
./mvnw clean test

# Iniciar o servidor Spring Boot (Porta 8080)
./mvnw spring-boot:run
```

A API estará disponível em:
- **Base API:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **Console H2:** `http://localhost:8080/api/h2-console`

### B. Executando o Frontend (Angular)
```bash
cd frontend

# Instalar dependências
npm install

# Iniciar o servidor de desenvolvimento (Porta 4200)
npm start
```
Acesse no seu navegador: 👉 **`http://localhost:4200`**

### C. Executando tudo via Docker Compose
Para subir o banco de dados PostgreSQL, o backend e o frontend juntos:
```bash
docker compose up --build
```

---

## 🧪 5. Validação dos Algoritmos de Negócio

Os 5 algoritmos centrais contam com testes automatizados executados pelo Maven:
1. `AthleteRecommendationEngineTest`: Valida doses de proteína (2.0g/kg para hipertrofia, 2.2g/kg para cutting), saturação de creatina e montagem de stacks.
2. `StockReservationEngineTest`: Valida bloqueio de concorrência e rejeição matemática de *overselling*.
3. `SmartDiscountEngineTest`: Valida acúmulo de combos (15% OFF em Whey + Creatina), cupons e respeito estrito ao teto de 20% de FittCoins.
4. `FreightCalculationEngineTest`: Valida isenções de frete para Sul e Sudeste acima de R$ 199,00.
5. `LoyaltyFittCoinsEngineTest`: Valida cálculo de pontos ganhos e resgate de cashback.
