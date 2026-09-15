# MarcenariaPro — Backend

Sistema de Gestão para Marcenaria — TCC do curso de Tecnologia em Sistemas para
Internet/TADS, IFPR Campus Cascavel.

Backend em **Java 21 + Spring Boot 3.3.4**, banco de dados **MySQL 8.4 em Docker**,
autenticação **JWT** com perfis Administrador/Funcionário, e regras de negócio
implementadas conforme os Requisitos Funcionais (RF01–RF33) da Especificação de
Requisitos de Software.

---

## 1. Como executar (Docker — recomendado)

Pré-requisito: Docker e Docker Compose instalados.

```bash
# na raiz do projeto (onde está o docker-compose.yml)
docker compose up --build
```

Isso vai:
1. Subir o MySQL 8.4 em um container, na porta `3307` (host) → `3306` (container).
2. Buildar a aplicação Spring Boot (multi-stage: Maven + JRE Alpine) e subi-la na porta `8080`.
3. Ao iniciar, o `DataSeeder` cria automaticamente um usuário administrador padrão:

```
Email: admin@marcenariapro.com
Senha: admin123
```

**Altere essa senha imediatamente após o primeiro login** (endpoint de alteração de
senha está no `UsuarioService`, pode ser exposto via um novo endpoint se desejar).

Para derrubar tudo (mantendo os dados do banco):
```bash
docker compose down
```

Para derrubar e apagar também os volumes (dados do banco e uploads):
```bash
docker compose down -v
```

---

## 2. Como executar localmente (sem Docker, para desenvolvimento)

1. Suba **apenas o banco MySQL** via Docker:
   ```bash
   docker compose up -d mysql
   ```
2. Rode a aplicação com o profile `local` (já é o padrão no `application.yml`):
   ```bash
   mvn spring-boot:run
   ```
   A aplicação vai se conectar ao MySQL em `localhost:3307`.

> **Importante**: este projeto foi desenvolvido/gerado em um ambiente sem acesso ao
> Maven Central, então **não foi possível compilar/testar automaticamente** aqui.
> Rode `mvn clean compile` (ou abra no IntelliJ/VS Code) na sua máquina antes do
> primeiro uso. Se aparecer algum erro de compilação, ele deve ser pequeno
> (import faltando, etc.) — me envie a mensagem de erro que eu corrijo.

---

## 3. Estrutura do projeto

```
marcenariapro-backend/
├── docker-compose.yml       # MySQL + aplicação
├── Dockerfile                # build multi-stage (Maven -> JRE Alpine)
├── pom.xml
└── src/main/
    ├── resources/
    │   └── application.yml   # profiles: local / docker
    └── java/com/marcenariapro/
        ├── model/             # 15 entidades JPA (Usuario, Cliente, Projeto,
        │   └── enums/         #   Ambiente, Material, Orcamento, ItemOrcamento,
        │                      #   Pedido, Producao, Pagamento, AssistenciaTecnica,
        │                      #   MaterialAssistencia, Anexo, Agendamento,
        │                      #   PagamentoAssistencia) + enums de status
        ├── repository/        # Spring Data JPA
        ├── service/           # regras de negócio (RF01-RF33)
        ├── controller/        # endpoints REST
        ├── security/          # JwtUtil, JwtAuthFilter
        ├── config/            # SecurityConfig, DataSeeder
        ├── dto/                # requests/responses
        └── exception/          # tratamento global de erros
```

---

## 4. Autenticação

```
POST /api/auth/login
{
  "email": "admin@marcenariapro.com",
  "senha": "admin123"
}
```
Retorna um `token` JWT. Envie em todas as demais requisições:
```
Authorization: Bearer <token>
```

Cadastro de novo usuário (RF31/RF32):
```
POST /api/auth/registrar
{
  "nome": "Fulano",
  "email": "fulano@marcenariapro.com",
  "senha": "123456",
  "telefone": "45999999999",
  "perfil": "FUNCIONARIO"   // ou ADMINISTRADOR
}
```

---

## 5. Principais endpoints (visão geral)

| Módulo | Endpoint base | RFs |
|---|---|---|
| Autenticação | `/api/auth` | RF31, RF32 |
| Clientes | `/api/clientes` | RF01 |
| Projetos | `/api/projetos` | RF02 |
| Ambientes | `/api/ambientes` | RF03 |
| Materiais / Estoque | `/api/materiais` | RF04, RF20 |
| Orçamentos | `/api/orcamentos` | RF05–RF12 |
| Pedidos | `/api/pedidos` | RF13–RF15, RF23 |
| Produção | `/api/producao` | RF16–RF19, RF22 |
| Pagamentos | `/api/pagamentos` | RF21 |
| Assistência Técnica | `/api/assistencias-tecnicas` | RF25–RF30 |
| Usuários | `/api/usuarios` (apenas ADMIN) | RF32 |
| Relatórios | `/api/relatorios` (apenas ADMIN) | RF33 |

### Fluxo típico de uso (ponta a ponta)
1. `POST /api/clientes` — cadastra cliente (RF01)
2. `POST /api/projetos/cliente/{clienteId}` — cria projeto (RF02)
3. `POST /api/ambientes/projeto/{projetoId}` — cria ambiente com complexidade (RF03)
4. `POST /api/materiais` — cadastra materiais usados (RF04)
5. `POST /api/orcamentos/ambiente/{ambienteId}` — cria orçamento do ambiente (RF05)
6. `POST /api/orcamentos/{id}/itens` — adiciona materiais ao orçamento (RF06)
   — mão de obra (RF07) e valor final (RF09) são calculados automaticamente
7. `PATCH /api/orcamentos/{id}/margem-lucro` — define margem de lucro (RF08)
8. `POST /api/orcamentos/{id}/decisao` — aprova/reprova (RF10)
   — quando **todos** os orçamentos de um projeto são aprovados, um **Pedido é
   gerado automaticamente** (RF13)
9. `POST /api/pagamentos/pedido/{pedidoId}` — registra pagamentos (RF21)
10. `PATCH /api/producao/pedido/{pedidoId}/progresso` — inicia produção
    (bloqueado até 30% de pagamento confirmado — RF22) e acompanha percentual (RF16)
11. `POST /api/pedidos/{id}/liberar-entrega` — libera entrega somente após
    quitação total (RF23)
12. `POST /api/assistencias-tecnicas` — abre assistência técnica em projeto
    entregue (RF25), com controle de garantia (RF26), anexos (RF27), materiais
    usados (RF28), agendamento (RF29) e cobrança fora da garantia (RF30)

---

## 6. Regras de negócio implementadas (destaques)

- **RF07** — Mão de obra calculada automaticamente por tabela de complexidade do
  ambiente (BAIXA/MEDIA/ALTA/MUITO_ALTA) — ver `OrcamentoService.TABELA_MAO_DE_OBRA`.
  Ajuste os valores conforme a realidade da sua marcenaria.
- **RF11** — Job agendado (`@Scheduled`, todo dia à 1h) expira orçamentos vencidos
  automaticamente.
- **RF13** — Geração automática de pedido quando todos os ambientes de um projeto
  têm orçamento aprovado.
- **RF22** — Produção só pode começar após 30% do valor do pedido confirmado
  (percentual configurável em `application.yml`: `app.regras.percentual-pagamento-minimo-producao`).
- **RF23** — Entrega só é liberada após quitação total do pedido.
- **RF30** — Assistências fora da garantia geram cobrança automática (materiais +
  custo fixo de mão de obra); dentro da garantia, nenhuma cobrança é gerada.

---

## 7. Próximos passos sugeridos para o TCC

- Ajustar a tabela de custo de mão de obra (RF07) e o custo fixo de mão de obra
  da assistência técnica (RF30) para valores reais da marcenaria.
- Escrever testes unitários (JUnit + Mockito) para os services, especialmente
  `OrcamentoService`, `ProducaoService` e `PagamentoService`, que concentram as
  regras de negócio mais sensíveis — ótimo material para o capítulo de
  "Resultados/Testes" do artigo.
- Rodar `mvn clean compile` localmente para validar a compilação e ajustar
  eventuais pequenos erros.
- Conectar o frontend (React + Vite + TypeScript + Tailwind + shadcn/ui +
  Recharts, já definido em conversas anteriores) a esta API.
