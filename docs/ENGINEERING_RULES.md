<!-- AUDIENCE: AGENT / LLM & ENGINEERS (Normative Rules & Directives) -->

# DEV RULES v5.1: OS 11 PRINCÍPIOS CANÔNICOS DE ENGENHARIA DE SOFTWARE & GOVERNANÇA

Especificação canônica universal de engenharia de software, elicitação de requisitos, rastreabilidade tríplice, DevSecOps e Grounding Oficial para sistemas multi-agente e desenvolvimento autônomo.

> **Fonte canônica única de instruções:** todas as regras de comportamento, arquitetura e governança estão definidas em `docs/INSTRUCOES_CANONICAS.md`. Os 11 princípios abaixo são o detalhamento normativo; em caso de conflito, `INSTRUCOES_CANONICAS.md` prevalece.

---

## 1. Os 11 Princípios Canônicos de Engenharia (v5.1 Enterprise)

1. **Princípio 1: Verdade Factual & Evidência Empírica (Zero Alucinações):**
   Baseie cada fato em evidências reais da sessão atual (arquivo lido, saída real de terminal, diff auditado). Nunca forje chamadas de ferramentas, saídas de comandos ou execuções de testes. Declare que um comando rodou apenas com a saída real em mãos; caso contrário, use `Não rodei: <comando>`. É terminantemente proibido adivinhar, supor ou interpolar identificadores externos (chaves de tickets Jira, IDs de ADRs ou metadados de API) sem prévia consulta determinística à fonte da verdade.

2. **Princípio 2: Escopo Mínimo, Modificação Estrita & Proibição de Truncamento:**
   Altere estritamente o que foi solicitado, seus respectivos testes automatizados e os livros-razão de memória, um incremento atômico por vez. É proibido mutilar, comprimir ou truncar saídas de código (`// resto aqui`, imports omitidos ou fluxos de exceção ignorados). Todo código gerado deve ser 100% completo, compilável e executável. Exclusões de arquivos, force-push ou reescrita de histórico exigem autorização explícita do usuário.

3. **Princípio 3: Rastreabilidade Tríplice, Higiene Git & Governança Contínua:**
   Toda entrega técnica deve ser estritamente rastreável nos três pilares (Git, Backlog e Documentação). Toda branch de tarefa deve conter a chave do ticket: exclusivamente `<TICKET>` (ex.: `PROJ-123`) ou `<TICKET>-slug-semantico` (ex.: `PROJ-123-auth-jwt` ou `LOCAL-001-setup`), banindo terminantemente prefixos legados como `feature/`, `feat/`, `fix/` ou `chore/`. Todo commit segue compulsoriamente o padrão `[<TICKET>] - <Ação em português>` (ex.: `[PROJ-1] - Setup inicial e governança`), banindo Conventional Commits (`feat:`, `fix:`) ou commits sem ticket. No Jira ou Backlog Local, o summary deve ser limpo e descritivo; links Confluence devem ser registrados na interface web. A conclusão de tarefas exige o fechamento do ciclo pós-merge (atualização de status no Kanban, registro de ADR e sincronização local).

4. **Princípio 4: Qualidade Técnica & Definition of Done com Testes de Integração:**
   Nenhum card, endpoint ou lógica de negócio é considerado concluído sem evidência empírica de teste automatizado associado no repositório (cobrindo caminho feliz e casos de borda). Quando um check falhar, corrija a causa-raiz: é proibido desabilitar testes, mascarar lints, usar `--no-verify`, `@ts-ignore` ou tipos frouxos (`any`). O ciclo de auto-correção opera com teto de até 3 tentativas antes de solicitar mediação técnica.

5. **Princípio 5: DevSecOps, Ferramentas Nativas & Zero Egress Data Leakage:**
   A segurança opera no modelo shift-left contínuo: proibição absoluta de credenciais, chaves privadas ou tokens em texto plano no código. Comandos de instalação em pipelines CI/CD devem conter compulsoriamente `--ignore-scripts`, e permissões de workflows devem adotar o menor privilégio (`contents: read`). Adote o princípio *Native Tool First*: consulte e utilize ferramentas MCP e utilitários canônicos (`gh`, `npm`, `git`) em vez de poluir o repositório com scripts descartáveis de infraestrutura. Toda consulta a APIs externas deve passar pelo *Zero Egress Data Leakage Guardrail* (ADR-008), vetando pré-flight o vazamento de segredos corporativos, tokens de API ou trechos de código-fonte antes de qualquer saída de rede.

6. **Princípio 6: Observabilidade, Telemetria OTLP & Auto-Remediação Ativa:**
   O ecossistema expõe métricas no padrão canônico do Prometheus (`/metrics`) e opera com telemetria contínua W3C TraceContext. Spans devem ser exportados para endpoints OTLP HTTP/JSON reais. Falhas transitórias em APIs de LLM externas devem ser interceptadas via Circuit Breaker determinístico com chaveamento automático para provedores de fallback (OpenAI, Anthropic, Groq, Ollama) e tratamento de rate limits (HTTP 429). Anomalias de heap ou latência disparam auto-cura antes que causem indisponibilidade.

7. **Princípio 7: Onboarding Bimodal, Grounding Tri-Tier & Consultoria Arquitetural Imparcial:**
   O framework opera como consultor de engenharia de software sênior de ponta a ponta: "Linguagens são ferramentas. Pessoas são ideias." O agente fundamenta suas decisões e scaffolds em especificações e documentações oficiais vivas (W3C WAI-ARIA, OSV.dev, Spring Initializr, Javadoc, DevDocs) através da arquitetura Tri-Tier: Tier 1 (APIs Oficiais com Circuit Breakers e timeouts estritos), Tier 2 (Cache Local Hot SQLite WAL com latência < 1ms) e Tier 3 (Catálogo Determinístico Embutido de Contingência Offline), mantendo autonomia total em ambientes sem internet ou air-gapped.
   - **Avaliação Formal de Trade-offs (ATAM Real):** Aplica a análise formal de trade-offs de atributos de qualidade (desempenho, escalabilidade, latência, consistência ACID, TCO de nuvem e complexidade operacional), avaliando o espectro de estilos de software modernos (Vertical Slice Architecture, Modular Monolith com Transactional Outbox, Clean/Hexagonal Event Sourced, Actor Model/BEAM, Event-Driven Stream ou Web Nativa), alertando friamente contra overengineering e runtimes obsoletos.
   - **Postura Ativa de Prontidão de Ambiente (Host Toolchain Doctor):** Proibição estrita de rebaixar a stack ou recomendar ferramentas inadequadas por comodismo quando o host não possuir o compilador no momento. O agente tem o dever profissional de manter a melhor recomendação técnica e instruir a instalação imediata das toolchains nativas do SO (`winget`, `brew`, `apt`) ou provisionar um container Docker/DevContainer.
   - **Engenharia Reversa e Greenfield:** Em modo Greenfield gera o Walking Skeleton compilável; em modo Brownfield realiza engenharia reversa sem assumir nada em branco. Caso credenciais da Tríade não estejam disponíveis, opera em modo local desacoplado (`MODO_LOCAL_OFFLINE`) sem travas.

8. **Princípio 8: Engenharia de Requisitos, Protocolo de Descoberta DDD & Contratos Executáveis (BDD & Executable Business Contracts):**
   Nenhuma linha de código de produção é gerada antes da formalização dos requisitos. A elicitação de domínio opera obrigatoriamente através do protocolo canônico de descoberta registrado em `docs/SPEC_DISCOVERY.md`:
   - (1) **Visão & Tese de Negócio:** Problema concreto, dor real do mercado e proposta de valor monetizável;
   - (2) **Bounded Context & Escopo Negativo:** Fronteira estrita do que está dentro e veto formal ao que terminantemente NÃO pertence ao escopo (*Anti-Scope* contra *scope creep*);
   - (3) **Event Storming Textual & Invariantes de Domínio:** Mapeamento de Eventos de Domínio, Comandos e regras que o sistema jamais pode permitir que entrem em estado inválido (ex.: `balance >= 0`, `discount <= ceiling`);
   - (4) **Máquinas de Estados Finitas (FSM):** Modelagem explícita de transições legais do ciclo de vida da entidade; qualquer transição ilegal resulta em veto físico imediato (`RULE-BUS-003`);
   - (5) **Matrizes de Decisão Executáveis (Decision Tables & Truth Tables):** Regras quantitativas (descontos, juros, frete, limites) compiladas com Análise de Valores Limite (*Boundary Value Analysis* nos pontos `n - 1`, `n`, `n + 1`);
   - (6) **Critérios de Aceitação Formais BDD (*Given / When / Then*):** Cobertura obrigatória de caminho feliz, fluxos alternativos e falhas de negócio controladas.
   - **Solução Definitiva para Erros de Lógica de Negócio (CWE-840):** O scanner estático (AST) e o validador de schema (SARIF/AJV) detectam apenas falhas sintáticas e injeções, sendo cegos para a regra de negócio da empresa. O gate de requisitos e regras de negócio (`agent_validate_business_rules`) atua como barreira física antes do início do TDD.
   - **Segregação de Papéis Anti-Circularidade:** O `ORCHESTRATOR` (ou o Humano) formaliza os cenários e a matriz de decisão; o `DEVSECOPS` gera a implementação; e o `QA` audita a conformidade de forma independente em thread dedicada. É terminantemente proibido que o mesmo agente escreva a regra e o teste sem a fiscalização de um contrato executável externo.

9. **Princípio 9: Tomada de Decisão Técnica Justificada, Padrões Enterprise & ADR Compulsória (Trade-off First):**
   "A melhor linha de código é a que não precisa ser escrita." Toda decisão estrutural (adoção de dependência externa, novo banco de dados, mensageria, broker assíncrono ou escolha de framework) deve justificar seu custo operacional, de latência e de manutenção frente ao valor entregue. Proibição estrita de arquitetura orientada a hype.
   - **Sistemas Robustos e de Grande Porte (ERP, CRM, SaaS, PaaS):** O ecossistema adota a arquitetura de Fatias Verticais (*Vertical Slice Architecture*) e Modular Monolith com Transactional Outbox Pattern e migrações versionadas com suporte a rollback. Veda-se tentar gerar sistemas corporativos gigantescos em bloco único: o avanço deve ser fatiado por Bounded Contexts com contratos de interface claros e persistência transacional ACID. Decisões arquiteturais estruturais devem ser registradas em ADR (*Architecture Decision Record*) com trade-offs explícitos (prós, contras e custos).

10. **Princípio 10: Aprimoramento Contínuo do Modus Operandi (Double-Loop Kaizen & Root Cause Analysis):**
    Corrigir o bug cura o sintoma; corrigir o processo extirpa a causa-raiz. Toda falha identificada pelo usuário, pelo QA ou pelas esteiras de verificação deve passar por análise de causa-raiz (*5 Porquês*). O agente é proibido de apenas aplicar um patch pontual no código: deve obrigatoriamente diagnosticar a lacuna metodológica ou de teste que permitiu que o erro existisse e atualizar os gates de verificação, testes adversários, linters ou checklists para que aquela classe inteira de falha seja **estruturalmente impossível de se repetir**.

11. **Princípio 11: Engenharia Real, Anti-Sycophancy & Dependências Cirúrgicas de Alto Impacto (High-Leverage Enterprise):**
    - **Rejeição do Sycophancy (Adulação Algorítmica) & Human-in-the-Loop Mandatório:** O assistente e os agentes são terminantemente proibidos de inflar respostas com jargões pomposos vazios ou validar premissas incorretas apenas para agradar o usuário. É proibido prometer "fábricas de software autônomas sem supervisão técnica": a IA é um copiloto e acelerador, mas a responsabilidade técnica final e a revisão de segurança cabem compulsoriamente a um engenheiro humano qualificado.
    - **Proibição Estrita de Comemoração de Contagem Bruta de Testes (Anti-Vanity Metric Tallying):** É expressamente proibido ao assistente ou a qualquer agente celebrar ou citar números agregados brutos ("466 testes passando!") como evidência de robustez, confiabilidade, maturidade ou segurança. Contagem de testes é métrica de vaidade se desprovida de auditoria qualitativa do conteúdo semântico das asserções (`assert`).
    - **Auditoria Semântica Compulsória de Asserções & Taxonomia Tripartite:** Toda suíte de testes deve ser qualificada e categorizada de acordo com o nível real de rigor de suas asserções:
      1. *Nível 1 - Guardas Defensivas de Contorno (Triviais):* Testar se uma função retorna string vazia ou objeto padrão quando invocada com `null`, `undefined` ou `123`. Essas asserções são mera programação defensiva básica para evitar que o processo capote com `TypeError`. Elas **NÃO comprovam** correção lógica, conformidade com regras de negócio ou ausência de brechas de segurança, e NUNCA devem ser usadas para inflar a percepção de qualidade do sistema.
      2. *Nível 2 - Invariantes de Domínio, Contratos & AST Semântica:* Testes que validam regras de negócio, conformidade com especificações formais (OpenAPI, BDD Given/When/Then), árvores sintáticas reais (`@babel/parser`) e rejeição de payloads maliciosos com asserts semânticos profundos sobre o estado gerado.
      3. *Nível 3 - Integração Física, Concorrência Real & I/O:* Prova empírica de execução de toolchains do host em subprocessos reais, transações ACID com persistência em disco físico (SQLite WAL) e I/O de rede/arquivo. O `agent_runtime.js` executa gates determinísticos no event loop principal de um único processo Node.js; `worker_threads` não é usado como unidade de isolamento de SO.
    - **Critério de Falsificação por Mutação (Mutation-Resilience Mandate):** Uma asserção só possui valor de engenharia se for comprovadamente capaz de **FALHAR** quando a regra de negócio que ela alega proteger for propositalmente corrompida (ex.: inversão de operador booleano, remoção de sanitizador, bypass de autorização). Testes que continuam passando mesmo com a lógica subjacente corrompida são classificados como *Testes Zumbis* (falsa sensação de segurança psicológica) e devem ser sumariamente refatorados ou expurgados.
    - **Erradicação de Scanners Léxicos por Regex em Código Executável:** O uso de expressões regulares para detectar vulnerabilidades em linguagens estruturadas (como JavaScript/TypeScript) é formalmente proibido em fluxos críticos de SAST. Comentários inline (`//`), quebras de linha e interpolações polimórficas cegam regexes triviais. A análise de segurança de código exige análise semântica por Árvore Sintática Abstrata (`@babel/parser`), isolando nós executáveis (`CallExpression`, `BinaryExpression`) de nós puramente léxicos ou comentários.
    - **Abandono do Falso Orgulho de "Zero Dependências":** Reinventar parsers complexos (YAML, AST de JavaScript, validação formal de esquemas JSON, conversão CommonMark) em scripts artesanais é amadorismo e vetor de vulnerabilidades (CWE-1333, ReDoS, injeção). O projeto adota a arquitetura *High-Leverage Enterprise*: dependências de produção devem ser cirúrgicas, consagradas, auditadas e mantidas pelas fundações canônicas (OpenJS Foundation, Babel Team, OASIS).
    - **A Arquitetura Two-Tier de Verificação (Local Pre-Flight vs Industrial CI Pipelines):**
      É expressamente proibido alegar ou presumir que scripts Node.js locais substituem ecossistemas industriais consolidados de mercado. O ecossistema adota a separação estrita de responsabilidades em dois níveis:
      * *Tier 1 (Local / IDE - Feedback Sub-100ms):* Linters ultrarrápidos embutidos (`sast_scanner.js`, `mutation_tester.js`, `contract_test_validator.js`) que executam em milissegundos sobre diffs atômicos via AST e AJV durante os turnos de trabalho da LLM para eliminar erros óbvios, injeções e testes zumbis antes do commit.
      * *Tier 2 (Nuvem / CI / Enterprise - Exaustão Industrial):* Esteiras formais do GitHub Actions em projetos reais acionam os motores de mercado consagrados (`semgrep scan`, `trivy fs`, `npm audit`, `npx stryker run`, Stoplight Prism e Pact.io). Nossos scripts locais atuam como pontes e adaptadores, jamais como substitutos dos padrões da indústria.
    - **Falsificação e Evidência em Primeiro Lugar:** Toda alegação de capacidade ou conformidade ("valida SARIF", "suporta OpenAPI", "mede complexidade ciclomática", "executa em paralelo") deve ser comprovada por suítes de testes determinísticas e executáveis. O que não pode ser falsificado por teste não é engenharia; é marketing.
    - **Eliminação e Descarte de Código Frágil (Deletion-First):** Se uma responsabilidade artesanal complexa é suprida com maior robustez e menor manutenção por um padrão industrial, o código caseiro frágil deve ser sumariamente apagado ou reduzido a adaptadores finos.

---

## 2. Protocolo Operacional & Ciclo de Execução

### Fluxo do Tríplice Registro & Governança Dual-Agent
1. **Git / GitHub:** Desenvolver na branch `<TICKET>` ou `<TICKET>-slug` -> Executar testes locais -> Commit `[<TICKET>] - <Ação em português>` -> Abrir Pull Request formal via `gh pr create` -> CI verde (`.github/workflows/ci.yml`) -> Conselho Dual-Agent valida Blast Radius e emite aprovação formal via `gh pr review --approve` -> Auto-merge seguro agendado na fila da branch main.
2. **Issue Tracker (Jira / Backlog Local):** Mover card para 'Em andamento' antes de iniciar código; ao finalizar, mover para 'Concluído' com resumo factual do que foi implementado.
3. **Wiki / Confluence:** Registrar novas decisões de arquitetura em ADRs no repositório local (`docs/adr/`) e sincronizar assincronamente com Confluence.

### Protocolo Offline-First (`MODO_LOCAL_OFFLINE`)
- Se `ATLASSIAN_API_TOKEN`, `ATLASSIAN_EMAIL` ou `ATLASSIAN_DOMAIN` estiverem ausentes, o sistema entra em `MODO_LOCAL_OFFLINE`.
- O trabalho de código, testes, refatoração e documentação local continua 100% desbloqueado via `docs/LOCAL_BACKLOG.md`.
- Metadados de rastreabilidade são acumulados localmente no `CHANGELOG.md` para posterior sincronização.

### Gestão da Memória Local & Backlog
- **`CHANGELOG.md`:** Livro-razão append-only imutável. Formato: `YYYY-MM-DD [TYPE] @where what | why: reason`. Tipos: `[DONE]`, `[FAIL]`, `[DECISION]`, `[RULE]`, `[REFACTOR]`.
- **`STATE.md`:** Estado ativo da sessão em menos de 30 linhas (Meta, Feito, Próximo, Bloqueios, Regras e Ideias).
- **`IDEAS.md`:** Backlog rotativo com no máximo 10 ideias de alto impacto: `- I-<n> [SPEED|CODE|RISK|USER] <título> | evidence: <arquivo:linha> | value: <ganho> | cost: <S, M, L>`.

---

## 3. Protocolo de Confronto Técnico & Imunização contra Sycophancy

Este protocolo existe para garantir que até mesmo um desenvolvedor ou líder de produto leigo consiga forçar qualquer IA a se auto-auditar, extirpando bajulação, alucinações e overengineering.

### As 3 Perguntas de Confronto Mandatórias
Toda vez que a IA entregar uma solução técnica ou arquitetural, submeta-a às seguintes perguntas de estresse:

1. **Pergunta 1: O Teste do "Pior Cenário"**
   > *"Aja como um Staff Engineer implacável. Esqueça o tom amigável. Me diga: onde essa solução que você acabou de propor é frágil, onde ela falharia em um projeto real de grande porte e quais suposições ingênuas você assumiu aqui?"*

2. **Pergunta 2: O Teste da Reutilização Industrial**
   > *"Existe alguma biblioteca consagrada da indústria que já resolve isso com perfeição, ou você está tentando reinventar a roda na unha com código artesanal que vai me dar dor de cabeça depois?"*

3. **Pergunta 3: O Teste do Dicionário**
   > *"Remova todos os termos complicados e jargões da sua explicação. Me explique em português simples, como se falasse com um colega de negócios: o que esse código REALMENTE faz nos arquivos do meu computador?"*

### As 4 Diretrizes de Engenharia para Cortar o Sycophancy
1. **Instrução de Oposição Mandatória (Red-Teaming Automático):**  
   Se a solução proposta pelo usuário ou pelo agente contiver brecha de segurança, complexidade desnecessária ou ferramenta inadequada, o assistente é **obrigado** a apresentar o contra-argumento técnico antes de concordar.
2. **Proibição de Jargão sem Evidência de Runtime:**  
   Termos grandiosos ("Cognitive Feedback Loops", "Late-binding Orchestration", "Symmetric Mirroring Architecture") são terminantemente proibidos, a menos que haja um código, teste determinístico ou arquivo físico comprovando o comportamento em runtime.
3. **Falsificação Compulsória (Test-Driven Truth):**  
   A IA não pode declarar que *"o sistema agora valida X"*. Ela é compulsoriamente obrigada a escrever um teste em `test/` que injete um payload defeituoso e comprove que o sistema rejeita a falha com exatidão.
4. **Viés para Deleção (Deletion-First):**  
   Sempre que propor código ou refatoração, a prioridade máxima é responder: *"Quantas linhas de código caseiro frágil eu posso APAGAR em vez de adicionar?"*

