<!-- AUDIENCE: ARCHITECTS, DEVELOPERS & TECH LEADS -->

# 🏛️ ARCHITECTURE — Arquitetura Canônica do E-Commerce FittShop

> **Visão Técnica, Diagramas UML (Casos de Uso, Sequência, Estados, Componentes), Modelo de Domínio e DevSecOps**  
> **Versão:** 1.0.0 Enterprise  
> **Padrões:** Clean Architecture / Hexagonal Ports & Adapters / Event-Driven Ready / Reactive Frontend  
> **Stack:** Java 21 LTS + Spring Boot 3.3.x (Backend) | Angular 19+ Standalone (Frontend)

---

## 1. VISÃO GERAL DA ARQUITETURA

A arquitetura do **FittShop** foi concebida sob os princípios da **Clean Architecture** (Arquitetura Limpa) e **Domain-Driven Design (DDD)**, garantindo desacoplamento entre regras de negócio corporativas, orquestração de casos de uso e detalhes de infraestrutura (bancos de dados, gateways e frameworks).

```mermaid
graph TD
    subgraph Frontend - Angular 19+
        UI_STORE[Loja Virtual / Catálogo Atleta]
        UI_RECOM[Wizard NutriFit IA]
        UI_CART[Carrinho & Checkout Reativo]
        UI_BACKOFFICE[Painel de Operações: Estoque, Caixa & Vendedoras]
    end

    subgraph API Gateway / Segurança
        CORS[CORS Filter]
        JWT_AUTH[Spring Security 6 + JWT RBAC]
        RATE_LIMIT[Rate Limiter & Idempotency Filter]
    end

    subgraph Backend - Spring Boot 3 Clean Architecture
        subgraph Adapters In - Controllers REST
            CTRL_PROD[ProductController]
            CTRL_ORDER[OrderCheckoutController]
            CTRL_RECOM[RecommendationController]
            CTRL_INV[InventoryController]
            CTRL_AUTH[AuthController]
        end

        subgraph Application Services / Use Cases
            SVC_CHECKOUT[CheckoutService]
            SVC_RECOM[RecommendationService]
            SVC_INV[InventoryService]
            SVC_LOYALTY[LoyaltyService]
        end

        subgraph Domain Algorithms & Core Entities
            ALG_RECOM[AthleteRecommendationEngine]
            ALG_INV[StockReservationEngine]
            ALG_DISCOUNT[SmartDiscountEngine]
            ALG_FREIGHT[FreightCalculationEngine]
            ALG_LOYALTY[LoyaltyFittCoinsEngine]
            MODELS[Product, Order, InventoryReservation, Customer, LoyaltyAccount]
        end

        subgraph Adapters Out - Persistence & External
            REPO_PROD[ProductRepository JPA]
            REPO_ORDER[OrderRepository JPA]
            REPO_INV[InventoryRepository JPA]
            GW_PIX[PixPaymentGatewayAdapter]
        end
    end

    subgraph Data & Storage
        DB[(PostgreSQL / H2 In-Memory)]
    end

    UI_STORE --> CORS
    UI_RECOM --> CORS
    UI_CART --> CORS
    UI_BACKOFFICE --> CORS
    CORS --> JWT_AUTH
    JWT_AUTH --> RATE_LIMIT
    RATE_LIMIT --> CTRL_PROD
    RATE_LIMIT --> CTRL_ORDER
    RATE_LIMIT --> CTRL_RECOM
    RATE_LIMIT --> CTRL_INV
    RATE_LIMIT --> CTRL_AUTH

    CTRL_ORDER --> SVC_CHECKOUT
    CTRL_RECOM --> SVC_RECOM
    CTRL_INV --> SVC_INV

    SVC_CHECKOUT --> ALG_INV
    SVC_CHECKOUT --> ALG_DISCOUNT
    SVC_CHECKOUT --> ALG_FREIGHT
    SVC_CHECKOUT --> ALG_LOYALTY
    SVC_RECOM --> ALG_RECOM

    SVC_CHECKOUT --> REPO_ORDER
    SVC_CHECKOUT --> REPO_INV
    SVC_CHECKOUT --> GW_PIX
    SVC_INV --> REPO_INV

    REPO_PROD --> DB
    REPO_ORDER --> DB
    REPO_INV --> DB
```

---

## 2. DIAGRAMA DE CASOS DE USO (USE CASE)

O sistema integra tanto a experiência de compra do atleta no e-commerce quanto o fluxo operacional físico da loja originado do mapeamento inicial (`projeto.drawio` e `FUNCIONALIDADES V2.docx`).

```mermaid
graph LR
    actorCliente((Atleta / Cliente))
    actorEstoquista((Estoquista))
    actorCaixa((Operador de Caixa))
    actorVendedora((Vendedora / Consultora))
    actorGerente((Gerente de Loja))

    subgraph E-Commerce FittShop
        UC1(Consultar Catálogo & Fichas Nutricionais)
        UC2(Executar Wizard NutriFit de Recomendação)
        UC3(Gerenciar Carrinho com Combos Inteligentes)
        UC4(Finalizar Checkout com Reserva Atômica)
        UC5(Pagar via PIX / Cartão / Boleto)
        UC6(Resgatar FittCoins & Acompanhar Pedido)
        UC7(Registrar Venda Assistida & Comissão)
        UC8(Validar Pagamentos & Emitir Comprovantes)
        UC9(Receber Fardos de Fornecedor & Etiquetar)
        UC10(Separar Pedido Pago & Baixar Estoque)
        UC11(Gerenciar Preços, Metas e Relatórios)
    end

    actorCliente --> UC1
    actorCliente --> UC2
    actorCliente --> UC3
    actorCliente --> UC4
    actorCliente --> UC5
    actorCliente --> UC6

    actorVendedora --> UC7
    actorVendedora --> UC1

    actorCaixa --> UC8
    actorCaixa --> UC5

    actorEstoquista --> UC9
    actorEstoquista --> UC10

    actorGerente --> UC11
```

---

## 3. DIAGRAMA DE COMPONENTES

```mermaid
graph TB
    subgraph Frontend Layer - Angular
        cmpCatalog[CatalogModule / ProductListComponent]
        cmpWizard[NutriFitWizardComponent]
        cmpCart[CartDrawerComponent]
        cmpCheckout[CheckoutModalComponent]
        cmpBackoffice[BackofficeManagementComponent]
        srvCart[CartStateService - Signals]
        srvApi[ApiClientService - HttpClient]
    end

    subgraph Backend Layer - Spring Boot 3
        subgraph Web API
            cRest[REST Controllers with OpenAPI]
            cSecurity[SecurityFilterChain & JwtDecoder]
        end

        subgraph Application Domain
            sOrder[Order & Checkout Application Service]
            sInventory[Inventory Management Service]
            sRecom[Recommendation Engine Service]
            sLoyalty[FittCoins Loyalty Service]
        end

        subgraph Domain Algorithms
            aAtomicRes[StockReservationEngine - Lease Lock]
            aNutriAlg[AthleteRecommendationEngine]
            aDiscounts[SmartDiscountEngine]
        end

        subgraph Infrastructure
            iPersistence[Spring Data JPA Repositories]
            iScheduler[ReservationExpiryCronScheduler]
            iDatabase[(PostgreSQL Database)]
        end
    end

    cmpCatalog --> srvApi
    cmpWizard --> srvApi
    cmpCart --> srvCart
    srvCart --> srvApi
    cmpBackoffice --> srvApi

    srvApi -->|JSON / HTTPS| cSecurity
    cSecurity --> cRest
    cRest --> sOrder
    cRest --> sInventory
    cRest --> sRecom

    sOrder --> aAtomicRes
    sOrder --> aDiscounts
    sOrder --> sLoyalty
    sRecom --> aNutriAlg

    sOrder --> iPersistence
    sInventory --> iPersistence
    iScheduler --> sInventory
    iPersistence --> iDatabase
```

---

## 4. DIAGRAMA DE SEQUÊNCIA: CHECKOUT, RESERVA ATÔMICA & PAGAMENTO

Este diagrama detalha a execução determinística do checkout com prevenção de concorrência e lease temporário de 15 minutos:

```mermaid
sequenceDiagram
    autonumber
    actor Atleta as Atleta / Cliente
    participant UI as Angular Frontend
    participant API as OrderCheckoutController
    participant Svc as CheckoutService
    participant InvAlg as StockReservationEngine
    participant Repo as InventoryRepository (DB)
    participant Gateway as PixGatewayAdapter
    participant Cron as ExpiryCronScheduler

    Atleta->>UI: Clica em "Finalizar Pedido com PIX"
    UI->>API: POST /api/checkout (itens, endereco, cpf, fittCoins)
    API->>Svc: processCheckout(request)
    
    activate Svc
    Svc->>InvAlg: reserveItems(cartItems, TTL=900s)
    activate InvAlg
    InvAlg->>Repo: SELECT FOR UPDATE / Optimistic Lock
    Note over InvAlg,Repo: Valida saldo físico - reservas ativas
    alt Saldo Insuficiente
        InvAlg-->>Svc: throw InsufficientStockException
        Svc-->>API: 409 Conflict (Produto X Indisponível)
        API-->>UI: Exibe alerta amigável de estoque esgotado
    else Saldo Suficiente
        InvAlg->>Repo: INSERT INTO stock_reservation (status='ACTIVE', expires_at=now+15m)
        InvAlg-->>Svc: ReservationResult(reservationId, expiresAt)
    end
    deactivate InvAlg

    Svc->>Gateway: createPixCharge(orderId, amount)
    Gateway-->>Svc: PixCharge(qrCodeBase64, copiaECola, txId)
    Svc->>Repo: INSERT INTO orders (status='AGUARDANDO_PAGAMENTO', reservation_id)
    Svc-->>API: CheckoutResponse(orderId, pixDetails, expiresAt)
    deactivate Svc
    API-->>UI: 201 Created (Exibe QR Code na Tela)
    UI-->>Atleta: Apresenta QR Code & Contagem Regressiva (15:00)

    par Fluxo de Pagamento Aprovado
        Atleta->>Gateway: Paga via App do Banco
        Gateway->>API: POST /api/webhooks/pix (txId, status='PAID')
        API->>Svc: confirmPayment(orderId)
        Svc->>Repo: UPDATE orders SET status='PAGO'
        Svc->>InvAlg: commitReservation(reservationId)
        InvAlg->>Repo: UPDATE stock SET quantity = quantity - reserved; DELETE reservation
        Svc->>Repo: INSERT INTO loyalty_transaction (+FittCoins)
        API-->>Gateway: 200 OK
    and Fluxo de Timeout / Não Pago
        Cron->>Svc: checkExpiredReservations()
        Note over Cron,Svc: Se passar 15 minutos sem pagamento:
        Svc->>InvAlg: releaseExpiredLease(reservationId)
        InvAlg->>Repo: UPDATE orders SET status='CANCELADO'; DELETE reservation
    end
```

---

## 5. DIAGRAMA DE SEQUÊNCIA: MOTOR NUTRIFIT DE RECOMENDAÇÃO

```mermaid
sequenceDiagram
    autonumber
    actor Atleta as Atleta / Cliente
    participant UI as NutriFitWizardComponent
    participant API as RecommendationController
    participant Svc as RecommendationService
    participant Alg as AthleteRecommendationEngine
    participant Repo as ProductRepository

    Atleta->>UI: Informa: Objetivo (Hipertrofia), Peso (80kg), Altura (1.80m), Treinos/sem (5x)
    UI->>API: POST /api/recommendation/calculate (AthleteProfileDto)
    API->>Svc: generatePlan(profile)
    activate Svc
    Svc->>Alg: calculateMacrosAndStack(profile)
    Note over Alg: IMC = 24.69 | Proteína diária = 160g (2.0g/kg)
    Note over Alg: Identifica Stack: Whey 100% Pure + Creatina Creapure + Shaker Térmico
    Alg-->>Svc: CalculatedStack(recommendedCategories, macros, discountComboRate=0.15)
    Svc->>Repo: findTopByCategories(recommendedCategories)
    Repo-->>Svc: List<Product>
    Svc-->>API: RecommendationResponse(profileSummary, products, comboDiscountPrice, savings)
    deactivate Svc
    API-->>UI: 200 OK (Apresenta Combo Personalizado)
    UI-->>Atleta: Exibe "Combo Hipertrofia Recomendado: De R$ 289 por R$ 245 (-15%)"
```

---

## 6. DIAGRAMA DE ESTADOS (FSM) — CICLO DE VIDA DO PEDIDO & ESTOQUE

```mermaid
stateDiagram-v2
    [*] --> CRIADO: Pedido gerado
    CRIADO --> AGUARDANDO_PAGAMENTO: Reserva Atômica Alocada (TTL: 15 min)
    
    AGUARDANDO_PAGAMENTO --> PAGO: Pagamento Confirmado (PIX/Cartão)
    AGUARDANDO_PAGAMENTO --> EXPIRADO: TTL 15m Esgotado
    EXPIRADO --> CANCELADO: Liberação Automática do Estoque
    
    PAGO --> EM_SEPARACAO: Enviado para Estoquista
    EM_SEPARACAO --> EXPEDIDO: Fardos Embalados & Código de Rastreio
    EXPEDIDO --> ENTREGUE: Transportador confirma entrega
    
    PAGO --> CANCELADO: Estorno / Desistência (Estoque Devolvido)
    CANCELADO --> [*]
    ENTREGUE --> [*]
```

---

## 7. MODELO DE DADOS / DIAGRAMA DE CLASSES DE DOMÍNIO

```mermaid
classDiagram
    class Product {
        +Long id
        +String sku
        +String name
        +String description
        +BigDecimal price
        +String category
        +String imageUrl
        +String nutritionalInfo
        +Integer stockQuantity
        +Integer minStockThreshold
        +Long version
    }

    class StockReservation {
        +Long id
        +String reservationUuid
        +Long productId
        +Integer quantity
        +LocalDateTime createdAt
        +LocalDateTime expiresAt
        +String status
    }

    class Order {
        +Long id
        +String orderNumber
        +Long customerId
        +OrderStatus status
        +BigDecimal subtotal
        +BigDecimal discountAmount
        +BigDecimal freightAmount
        +BigDecimal totalAmount
        +PaymentMethod paymentMethod
        +String trackingCode
        +String sellerCode
        +LocalDateTime createdAt
    }

    class OrderItem {
        +Long id
        +Long orderId
        +Long productId
        +String productName
        +Integer quantity
        +BigDecimal unitPrice
        +BigDecimal totalPrice
    }

    class Customer {
        +Long id
        +String name
        +String email
        +String cpf
        +String phone
        +AthleteProfile athleteProfile
    }

    class LoyaltyAccount {
        +Long id
        +Long customerId
        +Integer fittCoinsBalance
        +String tierLevel
        +LocalDateTime updatedAt
    }

    Product "1" -- "0..*" StockReservation : possesses
    Order "1" -- "1..*" OrderItem : contains
    Customer "1" -- "0..*" Order : places
    Customer "1" -- "1" LoyaltyAccount : holds
```

---

## 8. MATRIZ DEVSECOPS & SEGURANÇA (OWASP TOP 10)

| Vetor de Risco | Mitigação Implementada no FittShop |
|:---|:---|
| **A01: Quebra de Controle de Acesso** | Spring Security 6 com RBAC granular (`@PreAuthorize("hasRole('STOCKIST')")`). |
| **A02: Falhas Criptográficas** | Senhas com BCrypt (cost factor 12); tokens JWT com chave HMAC-SHA256 rotativa. |
| **A03: Injeção (SQL/NoSQL/Command)** | Spring Data JPA com PreparedStatements parametrizados; zero uso de `eval()` ou comandos em shell sem sanitização. |
| **A04: Design Inseguro (Race Conditions)** | Bloqueio otimista (`@Version`) no estoque e reservas atômicas com TTL estrito. |
| **A05: Configuração Insegura** | Headers HTTP de segurança (HSTS, X-Content-Type-Options: nosniff, CSP, X-Frame-Options: DENY). |
| **A07: Falhas de Identificação e Autenticação** | Proteção contra força bruta via Rate Limiting; expiração estrita de tokens JWT (60 minutos). |
| **A08: Falhas de Integridade de Software (Supply Chain)** | Travis/GitHub CI com `--ignore-scripts` e dependências fixadas no Maven e npm. |
