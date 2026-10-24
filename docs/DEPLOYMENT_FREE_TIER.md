# Guia de Hospedagem 100% Gratuita — FittShop

Este guia detalha como colocar o **FittShop** no ar em produção utilizando **planos gratuitos perpétuos**, **sem necessidade de cartão de crédito** e **sem custos escondidos**.

---

## 🏗️ Arquitetura de Hospedagem Gratuita (Zero Custo)

```
┌─────────────────────────────────┐
│     CLIENTE / NAVEGADOR         │
└────────────────┬────────────────┘
                 │
       HTTPS     ▼
┌─────────────────────────────────┐
│      VERCEL / NETLIFY           │
│  (Frontend Angular 18 SPA)      │
│  • Hospedagem Gratuita Ilimitada│
│  • CDN Global Ultra-Rápida      │
│  • SSL Grátis Automático        │
└────────────────┬────────────────┘
                 │  /api/*
       HTTPS     ▼
┌─────────────────────────────────┐
│        RENDER (Web Service)     │
│  (Backend Spring Boot 3 / Java) │
│  • Plano Gratuito (512 MB RAM)  │
│  • Deploy direto via Dockerfile │
│  • Suporta portas dinâmicas     │
└────────────────┬────────────────┘
                 │  JDBC SSL
       TCP       ▼
┌─────────────────────────────────┐
│     NEON.TECH ou SUPABASE       │
│  (PostgreSQL Gerenciado Cloud)  │
│  • Plano Gratuito Vitalício     │
│  • Sem necessidade de cartão    │
│  • 0.5 GB de armazenamento      │
│  • Flyway executa migrações     │
└─────────────────────────────────┘
```

---

## 🚀 Passo 1: Criar o Banco PostgreSQL Gratuito (30 segundos)

### Opção Recomendada: Neon Serverless Postgres (neon.tech)
1. Acesse [https://neon.tech](https://neon.tech) e faça login gratuito com sua conta do GitHub.
2. Crie um projeto chamado `fittshop-db` (selecione a região mais próxima, ex: *AWS US East (N. Virginia)* ou *São Paulo* se disponível).
3. Na tela inicial do painel, você verá a string de conexão (Connection String).
4. Copie a URL no formato:
   ```
   postgresql://fittuser:SEU_PASSWORD@ep-xyz.us-east-2.aws.neon.tech/fittshopdb?sslmode=require
   ```
5. Para o Spring Boot, a URL JDBC correspondente será:
   ```
   jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/fittshopdb?sslmode=require
   ```

*(Alternativa: [Supabase.com](https://supabase.com) — também oferece PostgreSQL gratuito perpétuo).*

---

## ⚙️ Passo 2: Hospedar o Backend no Render (render.com)

O **Render** oferece Web Services gratuitos que compilam diretamente do seu repositório GitHub via Docker.

1. Acesse [https://render.com](https://render.com) e crie sua conta gratuita com o GitHub.
2. No painel, clique em **"New +"** -> **"Web Service"**.
3. Conecte o repositório `gtrevizani/fittshop`.
4. Configure os campos:
   * **Name:** `fittshop-backend`
   * **Region:** Ohio ou Oregon (mesma região do banco se possível).
   * **Language / Environment:** `Docker`
   * **Dockerfile Path:** `backend/Dockerfile`
   * **Docker Context:** `backend`
   * **Instance Type:** `Free` (0.1 CPU, 512 MB RAM - $0/mês).
5. Na seção **Environment Variables** (Variáveis de Ambiente), adicione:
   * `SPRING_PROFILES_ACTIVE`: `prod`
   * `SPRING_DATASOURCE_URL`: `jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/fittshopdb?sslmode=require`
   * `SPRING_DATASOURCE_USERNAME`: seu usuário do Neon (ex: `fittuser`)
   * `SPRING_DATASOURCE_PASSWORD`: sua senha do Neon
   * `FITTSHOP_JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`
6. Clique em **"Deploy Web Service"**.
7. O Render irá construir o container e disponibilizar uma URL pública gratuita (ex: `https://fittshop-backend.onrender.com`).
   * As migrações do Flyway (`V1__init_schema.sql` e `V2__seed_production_catalog.sql`) rodarão automaticamente no primeiro boot!

> [!NOTE]
> No plano gratuito do Render, a aplicação entra em modo de suspensão após 15 minutos sem requisições e leva cerca de 40 segundos para "acordar" no primeiro acesso. Isso é o comportamento padrão para manter o serviço 100% gratuito.

---

## 🌐 Passo 3: Hospedar o Frontend no Vercel (vercel.com)

1. Acesse [https://vercel.com](https://vercel.com) e faça login gratuito com o GitHub.
2. Clique em **"Add New..."** -> **"Project"**.
3. Importe o repositório `gtrevizani/fittshop`.
4. Defina as configurações do projeto:
   * **Root Directory:** clique em Edit e selecione a pasta `frontend`.
   * **Framework Preset:** `Angular` ou `Other`.
   * **Build Command:** `npm run build`
   * **Output Directory:** `dist/fittshop-frontend/browser`
5. Na seção **Environment Variables**, configure a URL do seu backend recém-criado no Render:
   * `NG_APP_API_URL` ou no arquivo `vercel.json` para proxy reverso.
6. Clique em **"Deploy"**. Em cerca de 1 minuto seu e-commerce estará online com certificado SSL gratuito (ex: `https://fittshop.vercel.app`).

---

## 🔄 Resumo dos Custos

| Componente | Ferramenta | Custo | Cartão Exigido? |
| :--- | :--- | :--- | :--- |
| **Frontend SPA** | Vercel | **R$ 0,00** (Gratuito perpétuo) | ❌ Não |
| **Backend Java** | Render Web Service | **R$ 0,00** (Gratuito perpétuo) | ❌ Não |
| **Banco PostgreSQL**| Neon Serverless | **R$ 0,00** (Gratuito perpétuo) | ❌ Não |
| **Certificados SSL**| Let's Encrypt / Cloudflare | **R$ 0,00** (Automático) | ❌ Não |
| **Repositório Git** | GitHub | **R$ 0,00** | ❌ Não |
| **TOTAL** | — | **R$ 0,00** | **100% Free** |
