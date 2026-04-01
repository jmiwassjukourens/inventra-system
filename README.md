# Inventra

Internal operations platform for **inventory, purchasing, sales, and accounting**, delivered as a Spring Cloud microservices stack with a Next.js web application. The system centralizes product and stock data, supplier relationships, purchase and sales workflows, and customer account movements behind a single API gateway and enterprise authentication.

## Project overview

| | |
|---|---|
| **What it is** | A full-stack **inventory and business management** platform: catalog and stock, suppliers, purchases, sales, and accounts, with OAuth2-backed access. |
| **Problem it solves** | Replaces ad hoc spreadsheets and fragmented tools with **consistent APIs**, **auditable flows**, and **role-based access** for teams that need to run procurement and sales in one place. |
| **Who it is for** | **Internal operations, warehouse, and finance** teams in organizations that want a maintainable, service-oriented backend and a modern web UI—suitable as a portfolio piece or as a foundation for a production deployment. |

---

## Key features

- **Catalog** — Products and **stock** levels (`/api/products/**`, `/api/stocks/**`).
- **Suppliers** — Supplier master data and related APIs (`/api/suppliers/**`).
- **Purchases** — Purchase workflows and records (`/api/purchases/**`).
- **Sales** — Sales operations and internal sales endpoints (`/api/sales/**`, `/api/internal/sales/**`).
- **Accounts** — Customers, account balances, and internal movements (`/api/customers/**`, `/api/accounts/**`, `/api/internal/account-movements/**`).
- **Authentication** — Login and token flows via **Keycloak** and the **keycloak-adapter** (`/api/auth/**`).
- **Operations visibility** — **Spring Boot Admin** (optional UI) for health and instance discovery when the stack is running.

---

## Architecture

Inventra follows a **microservices** style: each business capability runs as its own Spring Boot application with its own MySQL schema. **Infrastructure services** provide discovery, configuration, routing, and identity.

1. **Clients** hit the **Next.js frontend** (browser) or call the **API Gateway** directly with a **Bearer token** issued by Keycloak.
2. The **API Gateway** (Spring Cloud Gateway) is the **only public HTTP entry** for REST traffic to business services. It validates **JWTs** (OAuth2 Resource Server) and routes by path to the correct service.
3. **Service discovery** uses **Netflix Eureka**: business services and the gateway **register** and the gateway resolves targets with **`lb://<service-id>`** (client-side load balancing per Eureka).
4. **Spring Cloud Config Server** serves **centralized configuration** from a Git repository (profile-specific YAML), so services can share settings without duplicating large property files in every image.
5. **MySQL** runs in Docker; each domain uses a dedicated database (see `init.sql`). **Keycloak** uses MySQL for its own realm and client configuration in dev-style setups.
6. **Internal networking**: all containers share a **Docker bridge network** (`inventra-net`). Services talk to each other by **DNS name** (e.g. `http://mysql:3306`, `http://eurekaserver:8761/eureka`).

This keeps **cross-cutting concerns** (routing, auth, config, discovery) at the edge and in shared components, while **domain logic** stays isolated per service.

---

## Tech stack

| Layer | Technologies |
|--------|----------------|
| **Backend** | Java 21, Spring Boot, Spring Cloud (Gateway, Config, Netflix Eureka), Spring Security OAuth2 Resource Server |
| **Frontend** | Next.js 14, React 18, TypeScript, Tailwind CSS, Radix UI, Axios, Recharts |
| **Data** | MySQL 8.4 (per-service schemas + Keycloak DB) |
| **Auth** | Keycloak 24 (OpenID Connect / OAuth2), dedicated **keycloak-adapter** service for auth-related APIs |
| **Infra** | Docker, Docker Compose, Maven (multi-module build), Spring Boot Admin |

---

## System components

### Business services

| Service | Responsibility |
|---------|------------------|
| **catalog** | Product catalog and stock; backs `/api/products/**` and `/api/stocks/**` via the gateway. |
| **suppliers** | Supplier data and operations; `/api/suppliers/**`. |
| **purchases** | Purchase orders and related logic; `/api/purchases/**`. |
| **sales** | Sales flows; `/api/sales/**` and `/api/internal/sales/**`. |
| **accounts** | Customers and accounting-style endpoints; `/api/customers/**`, `/api/accounts/**`, `/api/internal/account-movements/**`. |

Each service registers with **Eureka**, pulls config from the **Config Server**, and connects to **MySQL** using its own JDBC URL (see `docker-compose.yml`).

### Infrastructure services

| Component | Responsibility |
|-----------|------------------|
| **config-server** | Spring Cloud Config: Git-backed configuration for `SPRING_PROFILES_ACTIVE` (e.g. `local`). Exposed on **8888** in Compose. |
| **eurekaserver** | Service registry; peers and clients use **8761**. |
| **api-gateway** | Single entry for external API calls; JWT validation, route table to `catalog`, `suppliers`, `purchases`, `sales`, `accounts`, `keycloak-adapter`. **8080** on the host. |
| **keycloak-adapter** | Bridges application auth needs to Keycloak; routes under `/api/auth/**` on the gateway. Registers with Eureka like other services. |
| **frontend** | Next.js UI; configured to use the gateway as its API base (see build args / `API_GATEWAY_URL`). **3000** on the host. |
| **springbootadmin** | Optional dashboard for registered Spring Boot instances (Eureka-aware). **8090** on the host. |

### Supporting runtime

| Component | Role |
|-----------|------|
| **mysql** | Primary database; host port **3307** → container **3306** (avoids clashing with a local MySQL). |
| **keycloak** | Identity provider; host port **8180** → container **8080**. |

---

## Authentication

- **Keycloak** runs as a container and stores realm data in **MySQL** (`keycloak_db`).
- End users and API clients obtain tokens from Keycloak (OpenID Connect). The **API Gateway** acts as an **OAuth2 Resource Server** and validates JWTs using the **issuer URI** (e.g. `http://keycloak:8080/realms/inventra` inside the network; host access uses port **8180**).
- The **frontend** typically redirects users through Keycloak’s login or uses flows compatible with your realm/client setup; API calls include **`Authorization: Bearer <access_token>`**.
- The **keycloak-adapter** centralizes auth-related HTTP surface under **`/api/auth/**`** so the gateway can route auth traffic consistently alongside business APIs.

---

## How to run the project

### Prerequisites

- **Docker Engine** and **Docker Compose** (Compose V2 plugin recommended).
- **Git** (Config Server clones a remote config repository by default).
- Optional: **.env** file at the repository root for secrets and overrides (see `.env.example`).

### Configuration

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and set at least:
   - **`GIT_USERNAME` / `GIT_TOKEN`** if the config repo is private (Spring Cloud Config uses them for Git access).
   - **Keycloak admin and hostname** variables expected by Compose (see `docker-compose.yml`): e.g. `KEYCLOAK_ADMIN`, `KEYCLOAK_ADMIN_PASSWORD`, `KC_HOSTNAME`, and optionally `KEYCLOAK_ISSUER_URI` / `KEYCLOAK_*` for clients if you customize them.

3. Ensure **`REPOSITORY_PATH`** points to a reachable **Spring Cloud Config** Git repository compatible with this project’s service names and profiles.

### Build and start

From the repository root:

```bash
docker compose up --build
```

First startup can take several minutes while images build and MySQL initializes. Services declare **health checks** and **depends_on** conditions so the gateway and frontend start after core dependencies are healthy.

### Useful URLs (default Compose ports)

| Resource | URL |
|----------|-----|
| Web UI | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Config Server | http://localhost:8888 |
| Keycloak | http://localhost:8180 |
| Spring Boot Admin | http://localhost:8090 |
| MySQL (host) | `localhost:3307` (user/password from `.env` or defaults in Compose) |

---

## API access

All **business and auth HTTP APIs** intended for external use should go through the **API Gateway** (`http://localhost:8080` locally). Downstream services are not meant to be exposed directly in this layout.

**Example paths** (prefix with the gateway base URL; protected routes require a valid JWT unless your config marks them permit-all):

| Area | Example path |
|------|----------------|
| Auth (adapter) | `GET/POST http://localhost:8080/api/auth/...` |
| Catalog | `http://localhost:8080/api/products/...`, `http://localhost:8080/api/stocks/...` |
| Suppliers | `http://localhost:8080/api/suppliers/...` |
| Purchases | `http://localhost:8080/api/purchases/...` |
| Sales | `http://localhost:8080/api/sales/...` |
| Accounts | `http://localhost:8080/api/customers/...`, `http://localhost:8080/api/accounts/...` |

**Health checks** (typical for Spring Boot): `http://localhost:8080/actuator/health` on the gateway.

---

## Future improvements

- **Observability** — OpenTelemetry traces, structured logging aggregation (ELK / Loki), and metrics (Prometheus + Grafana) with SLO dashboards.
- **Resilience** — Circuit breakers (e.g. Resilience4j) and bulkheads on gateway-to-service calls; idempotency keys on write APIs.
- **CI/CD** — Pipeline to build multi-module Maven artifacts, scan images, and deploy Compose or Kubernetes manifests per environment.
- **Testing** — Contract tests (Spring Cloud Contract / Pact), gateway integration tests, and frontend E2E against a disposable Compose stack.
- **Security hardening** — Secrets in a vault, non-dev Keycloak profiles, mTLS between services if traffic leaves the bridge network.
