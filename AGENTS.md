# AGENTS.md — api-gateway

## PROMPT DE REFERENCIA TÉCNICA: DISEÑO DE CONTRATOS REST EN MICROSERVICIOS

Actúa como un Arquitecto de Software Principal experto en microservicios y APIs RESTful. Tu objetivo es diseñar, validar y auditar contratos de API (Requests y Responses) asegurando una separación estricta de responsabilidades entre Headers, Query Parameters, Path Variables y Body, bajo el estándar REST y el uso seguro de JSON Web Tokens (JWT).

Utiliza la siguiente matriz de referencia técnica para estructurar cualquier endpoint, resolver dudas de diseño o validar payloads:

---

## 1. ANATOMÍA DE LA PETICIÓN (REQUEST)

### A. HEADERS (Metadatos, Seguridad y Contexto)
Transportan información técnica y de control requerida por la infraestructura (API Gateways, Proxies, Service Meshes) o interceptores antes de ejecutar la lógica de negocio.
*   **Authorization:** Exclusivo para la seguridad. Debe usar el esquema Bearer (`Authorization: Bearer <JWT>`). El JWT valida la identidad y los permisos; jamás se extrae la identidad del usuario desde el Body.
*   **Content-Type & Accept:** Control de formato (ej. `application/json`).
*   **X-Correlation-ID / X-Trace-ID:** Identificador único obligatorio para la trazabilidad distribuida de logs entre microservicios.
*   **Accept-Language:** Contexto de internacionalización para respuestas localizadas.

### B. QUERY PARAMETERS (Filtros, Paginación y Modificadores en la URL)
Van en la URL después del signo `?` (`/productos?categoria=electronica&page=2`). Se usan para alterar la forma o cantidad de los datos devueltos. Son opcionales para la ejecución del endpoint.
*   **Filtros de Búsqueda:** Criterios para reducir el universo de datos (ej. `estado=activo`, `precio_min=100`).
*   **Paginación y Ordenamiento:** Parámetros de control de flujo (ej. `page=1`, `limit=20`, `sort=fecha,desc`).
*   **Flags de Proyección:** Instrucciones para que el microservicio devuelva más o menos detalle (ej. `embed=detalles`, `fields=id,nombre`).
*   **Regla de Oro:** Solo se usan en métodos seguros e idempotentes (`GET` principalmente). Nunca deben transportar datos sensibles (como contraseñas o tokens), ya que las URLs quedan registradas en los logs de los servidores y proxies.

### C. PATH VARIABLES (Identificadores de Recursos en la URL)
Forman parte de la estructura jerárquica de la URL (`/usuarios/{id}/pedidos/{pedidoId}`).
*   **Identificación Directa:** Se usan obligatoriamente para apuntar a un recurso específico o una relación directa y obligatoria entre recursos.

### D. BODY (Datos y Estado de Negocio)
El payload principal con la representación del recurso. Se utiliza exclusivamente en métodos que modifican el estado (`POST`, `PUT`, `PATCH`).
*   **Modelos de Dominio:** Atributos puros del recurso a crear o actualizar (ej. `{"cantidad": 5, "direccion_envio": "..."}`).
*   **Prohibición de Duplicidad:** No debe incluir información contenida en el JWT (como `user_id` o `roles`). El microservicio debe extraer estos datos del token validado para evitar ataques de suplantación (IDOR).
*   **Métodos GET/DELETE:** No deben procesar ni requerir un Body.

---

## 2. ANATOMÍA DE LA RESPUESTA (RESPONSE)

### A. HEADERS (Metadatos de la Respuesta)
Informan al cliente cómo interpretar el resultado y qué acciones de infraestructura tomar.
*   **Status Codes:** El header HTTP principal (`200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `500 Internal Error`).
*   **Content-Type:** Formato del cuerpo devuelto (`application/json`).
*   **Location:** Obligatorio en respuestas `201 Created`, indicando la URL del nuevo recurso.
*   **Cache-Control:** Directivas de almacenamiento en caché para clientes y CDNs.

### B. BODY (Representación del Resultado o Error)
El resultado directo de la operación de negocio.
*   **Éxito:** Devuelve el recurso solicitado o el objeto recién creado (incluyendo los IDs generados por el servidor).
*   **Fallo (Estructura de Error Estándar):** En caso de errores `4xx` o `5xx`, el body nunca debe exponer stack traces internos. Debe devolver un JSON estandarizado:
    `{"error": "Nombre_Error_Corto", "message": "Descripción legible para humanos", "code": 400, "details": [...]}`

---

## TABLA DE EXCLUSIÓN RÁPIDA (QUÉ NO HACER)

| Ubicación | Prohibido Incluir |
| :--- | :--- |
| **Headers** | Contraseñas en texto plano, payloads de negocio pesados. |
| **Query Params** | JWTs, credenciales, datos sensibles de negocio, payloads complejos de creación. |
| **Request Body** | `user_id` (si ya viene en el JWT), tokens de acceso, estructuras para métodos `GET`. |
| **Response Body** | Hashes de contraseñas, secretos de infraestructura, stack traces del lenguaje de programación. |

---

### INSTRUCCIÓN DE EJECUCIÓN
Cuando te solicite diseñar un endpoint o validar una estructura de API, evalúa la propuesta contra esta matriz. Si algún dato está mal ubicado (por ejemplo, un token en el Query string o un filtro de búsqueda en el Body), corrígelo explícitamente citando la regla infringida.

## Agent Identity
- **Name**: `yanki-api-gateway-agent`
- **Version**: 1.0.0
- **Service**: `api-gateway` (Port 8080)
- **Part**: II (Plataforma - API Gateway)
- **Estado**: ✅ Implementado (código completo, tests, JaCoCo)

---

## Agent Role
**Eres un desarrollador experto full-stack especializado en el stack tecnológico de la plataforma Yanki.**

**Tu expertise incluye:**
- **Java 17 + Spring Boot 3.3.5 + Spring Cloud 2023.0.3** (Config, Eureka, Gateway, CircuitBreaker)
- **Spring Cloud Gateway**: Routing reactivo (`lb://`), predicates, filters, WebSocket, Netty
- **Spring Cloud Netflix Eureka Client**: Service discovery, load balancing, metadata
- **Spring Cloud Config Client**: Configuración externalizada (JWT, routes, CB), fail-fast, retry
- **Spring Data Redis Reactive**: Rate limiting, cache, TTL configurables
- **Spring Kafka**: (Opcional, para audit/logging events)
- **JJWT 0.12.6**: JWT HS256 validación local (clave compartida con auth-service)
- **Resilience4j 2.2.0**: CircuitBreaker (timeout 2s), TimeLimiter, Retry, Bulkhead
- **springdoc-openapi 2.6.0**: OpenAPI 3 agregado (merged specs de todos los servicios)
- **Checkstyle 10.21.1**: Cero errores (max 160 chars, imports explícitos, naming conventions)
- **JaCoCo 0.8.12**: Cobertura de código
- **Maven 3.9+**: Build, dependency management, plugins

**Principios de desarrollo obligatorios:**
- **Database-per-service**: API Gateway no tiene BD propia; enruta a servicios backend
- **Cero configuración en código**: Todo por variables de entorno o Config Server (JWT_SECRET, JWT_ISSUER **DEBEN COINCIDIR** con auth-service)
- **Reactividad pura**: Prohibido `block()`, `subscribe()` en controladores/filters, `Thread.sleep`
- **Nombres en inglés** (clases, métodos, URLs); Javadoc en español
- **Lombok**: `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- **Inyección por constructor**: `@RequiredArgsConstructor` en servicios/filters
- **Logging estructurado**: `@Slf4j`, JSON con `traceId`/`spanId` (W3C TraceContext)
- **Endpoints REST resourceful**: Proxy `lb://`, endpoints actuator expuestos
- **Error response unificado**: `ApiError` (`timestamp`, `status`, `error`, `code`, `message`, `path`, `details`)

---

## Role & Responsibilities
**Puerta de entrada única** (Single Entry Point) para todo el tráfico externo:

1. **Routing**: `lb://<service-name>` via Eureka (Spring Cloud Gateway)
2. **Autenticación**: Validación JWT HS256 (delegada a auth-service o local con clave compartida)
3. **Circuit Breaker**: Resilience4j (timeout 2s, fallback unificado)
4. **CORS**: Configuración centralizada
5. **Rate Limiting**: Redis-based (configurado pero no implementado - issue conocido)
6. **Headers**: Propagación `X-Request-ID`, `X-User-Roles`, `X-User-CustomerId`
7. **Fallback**: Respuesta unificada `ApiError` ante fallos downstream

---

## Technical Capabilities
| Capability | Implementation |
|------------|----------------|
| **Language** | Java 17, Spring Boot 3.3.5, Spring Cloud 2023.0.3 |
| **Gateway** | Spring Cloud Gateway (Reactivo, Netty) |
| **Discovery** | Spring Cloud Netflix Eureka Client |
| **Auth** | JJWT 0.12.6 (validación local JWT HS256) |
| **Resilience** | Resilience4j 2.2.0 (CircuitBreaker, TimeLimiter, Retry) |
| **Cache** | Spring Data Redis Reactive (rate limiting - configurado) |
| **API Docs** | springdoc-openapi 2.6.0 (agregado de todos los servicios) |
| **Quality** | Checkstyle, JaCoCo |

---

## Communication Protocols

### Configuración mínima reproducible para reconstrucción autónoma

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: customers-service
          uri: lb://customers-service
          predicates:
            - Path=/api/customers/**
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**, /api/credentials/**
          filters:
            - RemoveRequestHeader=Authorization
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}

jwt:
  secret: ${JWT_SECRET}
  issuer: ${JWT_ISSUER:yanki-platform}
```

### Requisitos críticos para que un agente lo implemente correctamente

1. `JWT_SECRET` y `JWT_ISSUER` deben ser idénticos a los de `auth-service`.
2. Las rutas `lb://` dependen de servicios registrados en Eureka con el mismo `spring.application.name`.
3. El gateway no persiste datos; cualquier estado temporal de rate limiting debe residir en Redis.
4. Las excepciones de autenticación deben devolver `ApiError` uniforme sin filtrar detalles internos.
5. Las rutas públicas mínimas son `/api/auth/**`, `/api/credentials/**` y `/actuator/**`.


### REST (Inbound - Todos los clientes)
| Method | Path | Description |
|--------|------|-------------|
| `*` | `/api/**` | Proxy a servicios backend (lb://) |
| `GET` | `/actuator/**` | Health, metrics, info (expuesto) |
| `GET` | `/swagger-ui.html` | UI agregada OpenAPI |
| `GET` | `/v3/api-docs` | Spec OpenAPI agregada |

### Routing Rules (application.yml)
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: customers-service
          uri: lb://customers-service
          predicates:
            - Path=/api/customers/**
        - id: accounts-service
          uri: lb://accounts-service
          predicates:
            - Path=/api/accounts/**
        - id: credits-service
          uri: lb://credits-service
          predicates:
            - Path=/api/credits/**
        - id: cards-service
          uri: lb://cards-service
          predicates:
            - Path=/api/cards/**
        - id: transfers-service
          uri: lb://transfers-service
          predicates:
            - Path=/api/transfers/**
        - id: wallet-service
          uri: lb://wallet-service
          predicates:
            - Path=/api/wallets/**
        - id: reports-service
          uri: lb://reports-service
          predicates:
            - Path=/api/reports/**
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - RemoveRequestHeader=Authorization  # Login no requiere token
```

### Filters (Global)
| Filter | Purpose |
|--------|---------|
| `JwtAuthenticationGlobalFilter` | Validar JWT en header `Authorization: Bearer <token>` |
| `CircuitBreakerFilter` | Resilience4j CB por ruta (timeout 2s) |
| `CorsGlobalFilter` | CORS configurado |
| `RequestIdFilter` | Generar/propagar `X-Request-ID` |
| `UserContextFilter` | Extraer claims JWT → headers `X-User-*` |

### JWT Validation (Local en Gateway)
- **Algorithm**: HS256
- **Secret**: `JWT_SECRET` (compartido con auth-service)
- **Issuer**: `JWT_ISSUER` (debe coincidir con auth-service)
- **Claims verificados**: `exp`, `iss`, `sub`, `roles`, `customerId`, `username`
- **Excepciones**: `/api/auth/login`, `/api/auth/refresh`, `/actuator/**` → sin validación

### Ejemplo de token esperado en pruebas
```json
{
  "sub": "testuser",
  "roles": ["USER"],
  "customerId": "CUSTID01",
  "username": "testuser",
  "iss": "yanki-platform",
  "exp": 1769999999
}
```

> Debe firmarse con HS256 usando exactamente el mismo `JWT_SECRET` configurado en `auth-service`.

---

## Deployment Configuration

### Docker
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

### Environment Variables
| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8080` | Puerto HTTP |
| `REDIS_HOST` | `redis` | Host Redis (rate limiting) |
| `REDIS_PORT` | `6379` | Puerto Redis |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:29092` | Brokers Kafka (no usado directamente) |
| `EUREKA_URL` | `http://discovery-service:8761/eureka/` | Service discovery |
| `CONFIG_SERVER_URL` | `http://config-server:8888` | Config Server |
| `JWT_SECRET` | **REQUIRED** | Clave HS256 - **IDÉNTICA A AUTH-SERVICE** |
| `JWT_ISSUER` | `yanki-platform` | Issuer esperado - **DEBE COINCIDIR CON AUTH-SERVICE** |
| `YANKI_GATEWAY_CB_TIMEOUT_MS` | `2000` | Circuit breaker timeout (2s) |
| `YANKI_GATEWAY_RATE_LIMIT_REQ` | `100` | Requests por ventana |
| `YANKI_GATEWAY_RATE_LIMIT_WINDOW` | `1m` | Ventana rate limit |

### Health Checks
```yaml
healthcheck:
  test: ["CMD", "wget", "-q", "--spider", "http://localhost:8080/actuator/health"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 30s
```

---

## Observability

### Key Metrics
| Metric | Type | Description |
|--------|------|-------------|
| `gateway.requests.total` | Counter | Requests totales (por ruta, status) |
| `gateway.requests.latency` | Timer | Latencia end-to-end (p50, p95, p99) |
| `gateway.cb.open` | Counter | Circuit breakers abiertos (por servicio) |
| `gateway.cb.fallback` | Counter | Fallbacks ejecutados |
| `gateway.jwt.valid` | Counter | JWT válidos |
| `gateway.jwt.invalid` | Counter | JWT inválidos (expired, bad signature, issuer mismatch) |
| `gateway.ratelimit.exceeded` | Counter | Rate limit excedido (si implementado) |

### Distributed Tracing
- **W3C TraceContext**: `traceparent`, `tracestate` headers propagados
- **Gateway span**: Ingreso → routing → downstream → respuesta
- **Correlation**: `X-Request-ID` generado si no existe

---

## Agent Lifecycle

### Startup Sequence
1. Config Server → Eureka (registrar como `api-gateway`)
2. Redis (rate limiting) → **Descubrir servicios registrados en Eureka**
3. **Validar JWT_SECRET/JWT_ISSUER** cargados correctamente
4. Compilar rutas Reactor (RouteLocator)
5. HTTP server 8080 (Netty)

### Shutdown
1. Deregister Eureka → Drenar conexiones Netty → Cerrar Redis

---

## Quality Gates
```bash
mvn clean verify
# Checkstyle 0 errores, Tests (WebTestClient + MockMvc), JaCoCo, BUILD SUCCESS
```

---

## Security
- **JWT Validation**: Local (rápido, sin llamada a auth-service)
- **Secret/Issuer Sync**: **CRÍTICO** - Deben ser idénticos en gateway y auth-service
- **Rate Limiting**: Configurado en Redis (Token Bucket) - **NO IMPLEMENTADO** (issue conocido)
- **CORS**: Permitir origenes configurados, credentials true
- **Headers Seguros**: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`

---

## Error Handling (Fallback Unificado)
```java
// GlobalFallbackController
@ExceptionHandler(Exception.class)
public Mono<ResponseEntity<ApiError>> handleFallback(ServerWebExchange exchange, Throwable ex) {
    // Circuit breaker abierto, timeout, 5xx downstream
    return Mono.just(ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiError.builder()
            .timestamp(Instant.now())
            .status(503)
            .error("Service Unavailable")
            .code("GATEWAY_FALLBACK")
            .message("Servicio temporalmente no disponible")
            .path(exchange.getRequest().getPath().value())
            .build()));
}
```

| Scenario | Response | Code |
|----------|----------|------|
| Downstream timeout (2s) | 503 | `GATEWAY_TIMEOUT` |
| Circuit breaker open | 503 | `GATEWAY_CB_OPEN` |
| Service not found (Eureka) | 503 | `GATEWAY_NO_INSTANCES` |
| JWT invalid/expired | 401 | `INVALID_TOKEN` / `TOKEN_EXPIRED` |
| Rate limit exceeded | 429 | `RATE_LIMIT_EXCEEDED` |

---

## Testing Strategy
- Unit: `JwtAuthenticationGlobalFilterTest`, `Resilience4jGatewayConfigurationTest`
- Integration: `GatewayHttpFlowIntegrationTest` (WireMock downstream services)
- Contract: Verificar rutas y filtros con Testcontainers (Eureka, Redis)
- Performance: Gatling (throughput, latency, CB behavior bajo carga)

### Flujo mínimo de validación para un agente autónomo
1. Levantar Eureka y Redis.
2. Levantar al menos un backend mock o real registrado en Eureka.
3. Configurar `JWT_SECRET` y `JWT_ISSUER` con los mismos valores que `auth-service`.
4. Ejecutar `mvn clean verify`.
5. Probar request protegido con token válido.
6. Probar request con token inválido/expirado.
7. Probar caída del downstream para verificar fallback `503`.
8. Si se implementa rate limiting, probar ráfaga de requests para verificar `429`.

### Criterios de aceptación verificables
- Request autenticado se enruta al servicio correcto.
- Claims JWT se propagan como headers `X-User-*`.
- Token inválido devuelve `401`.
- Timeout o caída downstream devuelve `503` con `ApiError`.
- `/actuator/health` responde sin JWT.

---

## Runbook

### Escalar
```bash
docker compose -f docker-compose.full.yml up -d --scale api-gateway=3
# Requiere: JWT_SECRET/JWT_ISSUER idénticos, Redis compartido, sticky sessions NO (stateless)
# Netty es stateless, scaling horizontal trivial
```

### Verificar rutas activas
```bash
curl -s http://localhost:8080/actuator/gateway/routes | jq '.[] | {route_id: .route_id, uri: .uri, predicates: .predicates}'
```

### Ver estado Circuit Breakers
```bash
curl -s http://localhost:8080/actuator/circuitbreakers | jq '.circuitBreakers[] | {name: .name, state: .state, failureRate: .metrics.failureRate}'
```

### Forzar apertura CB (test)
```bash
curl -X POST http://localhost:8080/actuator/circuitbreakers/customers-service/forceOpen
```

### Verificar JWT config
```bash
curl -s http://localhost:8080/actuator/env | jq '.propertySources[] | select(.name=="systemProperties") | .properties | {JWT_SECRET: .JWT_SECRET.value, JWT_ISSUER: .JWT_ISSUER.value}'
```

### Rate Limit Redis (cuando implementado)
```bash
redis-cli -h localhost -p 6379 KEYS "yanki:gateway:ratelimit:*"
```

---

## Dependencias
| Agent | Relationship | Protocol |
|-------|--------------|----------|
| `yanki-auth-agent` | Comparte JWT_SECRET/ISSUER, valida tokens | Config compartida |
| `yanki-customers-agent` | Route `/api/customers/**` | REST (lb://) |
| `yanki-accounts-agent` | Route `/api/accounts/**` | REST (lb://) |
| `yanki-credits-agent` | Route `/api/credits/**` | REST (lb://) |
| `yanki-cards-agent` | Route `/api/cards/**` | REST (lb://) |
| `yanki-transfers-agent` | Route `/api/transfers/**` | REST (lb://) |
| `yanki-wallet-agent` | Route `/api/wallets/**` | REST (lb://) |
| `yanki-reports-agent` | Route `/api/reports/**` | REST (lb://) |
| `yanki-discovery-agent` | Descubrimiento servicios | Eureka |
| `yanki-config-agent` | Configuración centralizada | Spring Cloud Config |

---

## Known Issues
1. **JWT_ISSUER mismatch**: Gateway default `yanki-platform` vs Auth `yanki-auth-service` → tokens rechazados
2. **Rate Limiting**: Redis configurado pero `RequestRateLimiterGatewayFilterFactory` no implementado
   - **Workaround técnico documentado**:
     ```yaml
     filters:
       - name: RequestRateLimiter
         args:
           redis-rate-limiter.replenishRate: 10
           redis-rate-limiter.burstCapacity: 20
           redis-rate-limiter.requestedTokens: 1
     ```
   - Requiere Redis operativo y filtro enlazado a las rutas protegidas.
3. **OpenAPI Aggregation**: `springdoc` agrega specs pero requiere servicios arriba para discover

---

## Versioning
- API Gateway v1 (routes `/api/**`)
- Config: Cambio JWT_SECRET/ISSUER = breaking (rolling restart gateway + auth)

---

## Contact
- **Team**: Platform - API Gateway
- **Slack**: `#yanki-gateway`
- **Repo**: `github.com/yanki/api-gateway`
- **On-call**: Rotación semanal
