<!-- AUDIENCE: HYBRID / AGENT & HUMAN (Innovation Backlog & Strategic Ideation) -->

# IDEAS (Backlog Vivo de Oportunidades & Inovação de Domínio)

Este documento registra o backlog rotativo de melhorias e hipóteses técnicas em aberto, auditadas pelo **AGENT_QA (Shift-Left Gatekeeper)** e estritamente aderentes à **Regra 14 (Filtro de Sanidade Contextual)** e às especificações de `docs/ENGINEERING_RULES.md`.

> **Fonte canônica única de instruções:** todas as regras de comportamento, arquitetura e governança estão definidas em `docs/INSTRUCOES_CANONICAS.md`. Este backlog (IDEAS.md) registra oportunidades de melhoria; em caso de conflito, `INSTRUCOES_CANONICAS.md` prevalece.

---

## 🎯 Backlog Aberto de Domínio (Até 10 Itens Ordenados por Valor)

| ID | Categoria | Oportunidade Técnica | Valor Mensurado | Custo | Status |
| :---: | :---: | :--- | :--- | :---: | :---: |
| **OPP-001** | `DX/UI` | **Token Optimizer Playground no Web Inspector (`/playground/tokens`)** | Visualização interativa e cópia rápida de esqueletos AST com cálculo de economia de tokens | `S` | Backlog |
| **OPP-002** | `QA/SPEED` | **Paralelização em Chunks no `MutationTester`** | Aceleração de testes de mutação semântica de 800ms para < 85ms via sandboxes `node:vm` reutilizáveis | `M` | Backlog |
| **OPP-003** | `SRE/METRICS` | **Contadores de Tokens no `/metrics` (Prometheus 0.0.4)** | Exposição de `agent_tokens_original_total` e `agent_tokens_saved_total` para dashboards Grafana | `S` | Backlog |

---

## 📜 Histórico de Infraestrutura Concluída (Entregas do Agent Master)

> **Nota de Governança (Regra 14):** As 15 hipóteses de infraestrutura do agente (I-1 a I-15: Servidor MCP, CLI, Revisor de PRs, Tracers, Circuit Breaker, etc.) foram concluídas, homologadas e arquivadas no livro-razão imutável [docs/CHANGELOG.md](./CHANGELOG.md). O `IDEAS.md` permanece dedicado exclusivamente ao backlog vivo do produto do usuário.
