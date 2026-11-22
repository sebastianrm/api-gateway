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
- **Resilience4j 2.2.0**: CircuitBreaker (TimeLimiter 10s, 30% failure rate), Retry
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

1. **Routing**: `lb://<service-name>` via Eureka (Spring Cloud Gateway) con `StripPrefix=0`
2. **Autenticación**: Validación JWT HS256 local (clave compartida con auth-service) + blacklist Redis
3. **Token Blacklist**: Consulta Redis (`yanki:auth:blacklist:<token>`) con patrón fail-open
4. **Circuit Breaker**: Resilience4j (TimeLimiter 10s, CB 30% failure rate, fallback unificado)
5. **CORS**: Configuración centralizada y externalizada (`yanki.gateway.cors.*`)
6. **Headers**: Propagación `X-User-Id`, `X-Username`, `X-Customer-Id`, `X-Roles`
7. **Error Handling**: `GatewayErrorHandler` unificado → `ApiError` JSON estandarizado
8. **Fallback**: `FallbackController` (`/fallback/**`) responde 503 ante fallos downstream

---

## Technical Capabilities
| Capability | Implementation |
|------------|----------------|
| **Language** | Java 17, Spring Boot 3.3.5, Spring Cloud 2023.0.3 |
| **Gateway** | Spring Cloud Gateway (Reactivo, Netty) |
| **Discovery** | Spring Cloud Netflix Eureka Client |
| **Auth** | JJWT 0.12.6 (validación local JWT HS256 + blacklist Redis) |
| **Resilience** | Resilience4j 2.2.0 (CircuitBreaker, TimeLimiter) |
| **Cache** | Spring Data Redis Reactive (token blacklist, fail-open) |
| **Error Handling** | GatewayErrorHandler + ErrorResponseWriter + FallbackController |
| **CORS** | CorsWebFilter reactivo (yanki.gateway.cors.*) |
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
  config:
    import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}
  cloud:
    gateway:
      discovery:
        locator:
          enabled: false
      httpclient:
        connect-timeout: 5000
        response-timeout: 10s
      routes:
        - id: auth-service-api
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: default
                fallbackUri: forward:/fallback/circuit-breaker
        - id: auth-service-login
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: default
                fallbackUri: forward:/fallback/circuit-breaker
        - id: customers-service
          uri: lb://customers-service
          predicates:
            - Path=/api/customers/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: default
                fallbackUri: forward:/fallback/circuit-breaker
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://discovery-service:8761/eureka/}
  instance:
    prefer-ip-address: true

resilience4j:
  timelimiter:
    configs:
      default:
        timeoutDuration: 10s
        cancelRunningFuture: true
  circuitbreaker:
    configs:
      default:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 30
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true

yanki:
  security:
    jwt:
      secret: ${JWT_SECRET:}
      issuer: ${JWT_ISSUER:yanki-platform}
      expiration: ${JWT_EXPIRATION:1h}
      max-lifetime-grace: ${JWT_MAX_LIFETIME_GRACE:5m}
    public-paths:
      - /api/auth/**
      - /auth/**
      - /actuator/**
      - /v3/api-docs/**
      - /swagger-ui/**
      - /swagger-ui.html
      - /fallback/**
  gateway:
    cors:
      allowed-origins: ${CORS_ALLOWED_ORIGINS:}
      allowed-methods: ${CORS_ALLOWED_METHODS:GET,POST,PUT,PATCH,DELETE,OPTIONS}
      allowed-headers: ${CORS_ALLOWED_HEADERS:Authorization,Content-Type,Accept,X-Request-ID,X-Correlation-ID}
      allow-credentials: ${CORS_ALLOW_CREDENTIALS:false}
      max-age: ${CORS_MAX_AGE:1h}

spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      timeout: 2s
```

### Requisitos críticos para que un agente lo implemente correctamente

1. `JWT_SECRET` y `JWT_ISSUER` deben ser idénticos a los de `auth-service`.
2. Las rutas `lb://` dependen de servicios registrados en Eureka con el mismo `spring.application.name`.
3. El gateway no persiste datos; la blacklist de tokens reside en Redis (clave `yanki:auth:blacklist:<token>`).
4. Las excepciones de autenticación deben devolver `ApiError` uniforme sin filtrar detalles internos.
5. Las rutas públicas mínimas son `/api/auth/**`, `/auth/**`, `/actuator/**` y `/fallback/**`.
6. El timeout del TimeLimiter es 10s (no 2s); el failure rate del CB es 30% (no 50%).
7. Las propiedades JWT usan el prefijo `yanki.security.jwt.*` (no `jwt.*`).


### REST (Inbound - Todos los clientes)
| Method | Path | Description |
|--------|------|-------------|
| `*` | `/api/**` | Proxy a servicios backend (lb://) |
| `GET` | `/actuator/**` | Health, metrics, info (expuesto) |
| `GET` | `/swagger-ui.html` | UI agregada OpenAPI |
| `GET` | `/v3/api-docs` | Spec OpenAPI agregada |

### Routing Rules (application.yml)

Cada ruta aplica `StripPrefix=0` y `CircuitBreaker` con `name: default` y `fallbackUri: forward:/fallback/circuit-breaker`.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service-api
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
        - id: auth-service-login
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
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
```

> **Nota:** Las rutas de auth-service (`/api/auth/**` y `/auth/**`) son públicas (no requieren JWT).
> El filtro `RemoveRequestHeader=Authorization` **no se aplica** en el gateway; la autenticación
> se gestiona mediante la lista de rutas públicas en `yanki.security.public-paths`.

### Filters (Global)
| Filter | Purpose |
|--------|---------|
| `JwtAuthenticationGlobalFilter` | Validar JWT HS256 + blacklist Redis; propagar `X-User-Id`, `X-Username`, `X-Customer-Id`, `X-Roles` |
| `GatewayErrorHandler` | `ErrorWebExceptionHandler` (@Order -2): traduce excepciones a `ApiError` JSON |
| `CorsWebFilter` | CORS reactivo configurado vía `yanki.gateway.cors.*` |

### Filtros por ruta (application.yml)
| Filter | Purpose |
|--------|---------|
| `StripPrefix=0` | Mantiene el path original al enrutar |
| `CircuitBreaker` | Resilience4j CB por ruta (TimeLimiter 10s, fallback `/fallback/**`) |

### JWT Validation (Local en Gateway)
- **Algorithm**: HS256
- **Secret**: `yanki.security.jwt.secret` (compartido con auth-service; mínimo 32 bytes)
- **Issuer**: `yanki.security.jwt.issuer` (debe coincidir con auth-service)
- **Expiration**: `yanki.security.jwt.expiration` (default: 1h)
- **Max Lifetime Grace**: `yanki.security.jwt.max-lifetime-grace` (default: 5m)
- **Claims verificados**: `exp`, `iss`, `sub`, `roles`/`authorities`/`scope`, `customerId`, `username`/`preferred_username`
- **Blacklist**: `TokenBlacklistService` consulta Redis (`yanki:auth:blacklist:<token>`) con fail-open
- **Rutas públicas**: configuradas en `yanki.security.public-paths` (sin validación JWT)

### Resolución de claims
| Claim | Prioridad |
|---|---|
| userId | `sub` → `userId` → `""` |
| username | `username` → `preferred_username` → `userId` |
| customerId | `customerId` → `""` |
| roles | `roles` → `authorities` → `scope` |

### Ejemplo de token esperado en pruebas
```json
{
  "sub": "testuser",
  "username": "testuser",
  "roles": ["USER"],
  "customerId": "CUSTID01",
  "iss": "yanki-platform",
  "iat": 1769996400,
  "exp": 1769999999
}
```

> Debe firmarse con HS256 usando exactamente el mismo `JWT_SECRET` configurado en `auth-service`.
> La vida del token (`exp - iat`) no debe superar `expiration + max-lifetime-grace`.

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
| `REDIS_HOST` | `localhost` | Host Redis (token blacklist) |
| `REDIS_PORT` | `6379` | Puerto Redis |
| `EUREKA_URL` | `http://discovery-service:8761/eureka/` | Service discovery (Eureka) |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config Server (Spring Cloud Config) |
| `JWT_SECRET` | **REQUIRED** | Clave HS256 (min 32 bytes) - **IDÉNTICA A AUTH-SERVICE** |
| `JWT_ISSUER` | `yanki-platform` | Issuer esperado - **DEBE COINCIDIR CON AUTH-SERVICE** |
| `JWT_EXPIRATION` | `1h` | Expiración máxima del token |
| `JWT_MAX_LIFETIME_GRACE` | `5m` | Tolerancia de desfase de reloj |
| `CORS_ALLOWED_ORIGINS` | (vacío) | Orígenes CORS permitidos |
| `CORS_ALLOWED_METHODS` | `GET,POST,PUT,PATCH,DELETE,OPTIONS` | Métodos CORS permitidos |
| `CORS_ALLOWED_HEADERS` | `Authorization,Content-Type,Accept,X-Request-ID,X-Correlation-ID` | Headers CORS |
| `CORS_ALLOW_CREDENTIALS` | `false` | Credenciales cross-origin |
| `CORS_MAX_AGE` | `1h` | Cache de preflight CORS |

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
- **JWT Validation**: Local (rápido, sin llamada a auth-service) con blacklist Redis
- **Secret/Issuer Sync**: **CRÍTICO** - `yanki.security.jwt.secret` y `yanki.security.jwt.issuer` deben ser idénticos en gateway y auth-service
- **Token Blacklist**: Redis (`yanki:auth:blacklist:<token>`) con patrón fail-open
- **CORS**: Configurado vía `yanki.gateway.cors.*` (externalizado)
- **Headers de identidad**: Se eliminan headers entrantes `X-User-*` para evitar suplantación
- **Min secret length**: 32 bytes para HS256 (validado en startup)

---

## Error Handling (Fallback Unificado)

La gateway centraliza el manejo de errores en tres componentes:

| Componente | Rol |
|---|---|
| `GatewayErrorHandler` | `ErrorWebExceptionHandler` (@Order -2): traduce excepciones a `ApiError` JSON |
| `ErrorResponseWriter` | Serializa `ApiError` a JSON (compartido por filter y errorHandler) |
| `FallbackController` | `/fallback/**`: responde 503 cuando el circuit breaker abre |

### Códigos de Error

| Scenario | Response | Code | Componente |
|----------|----------|------|---|
| Token ausente o mal formado | 401 | `MISSING_TOKEN` | `JwtAuthenticationGlobalFilter` |
| Token inválido, expirado o revocado | 401 | `INVALID_TOKEN` | `JwtAuthenticationGlobalFilter` |
| Servicio no disponible (Eureka, conexión) | 503 | `SERVICE_UNAVAILABLE` | `GatewayErrorHandler` / `FallbackController` |
| Downstream timeout (10s) | 504 | `GATEWAY_TIMEOUT` | `GatewayErrorHandler` |
| Circuit breaker abierto | 503 | `SERVICE_UNAVAILABLE` | `GatewayErrorHandler` |
| Error interno no controlado | 500 | `INTERNAL_ERROR` | `GatewayErrorHandler` |
| Método HTTP no permitido | 405 | `METHOD_NOT_ALLOWED` | `GatewayErrorHandler` |
| Recurso no encontrado | 404 | `NOT_FOUND` | `GatewayErrorHandler` |

### Formato de Error (`ApiError`)

```json
{
  "timestamp": "2026-11-22T13:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "code": "INVALID_TOKEN",
  "message": "El token JWT es invalido, ha expirado, no cumple la politica de seguridad o ha sido revocado",
  "path": "/api/accounts/123",
  "details": ["InvalidTokenException: Firma o claims invalidos: ..."]
}
```

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
5. Probar request protegido con token válido → verificar headers `X-User-Id`, `X-Username`, `X-Customer-Id`, `X-Roles`.
6. Probar request con token inválido/expirado → verificar `401 INVALID_TOKEN`.
7. Probar token revocado (en blacklist de Redis) → verificar `401 INVALID_TOKEN`.
8. Probar caída del downstream → verificar fallback `503 SERVICE_UNAVAILABLE`.
9. Probar ruta pública (`/api/auth/**`, `/actuator/**`) → verificar que no exige JWT.
10. Si se implementa rate limiting, probar ráfaga de requests para verificar `429`.

### Criterios de aceptación verificables
- Request autenticado se enruta al servicio correcto vía `lb://`.
- Claims JWT se propagan como headers `X-User-Id`, `X-Username`, `X-Customer-Id`, `X-Roles`.
- Token inválido devuelve `401` con código `INVALID_TOKEN`.
- Token revocado (en blacklist) devuelve `401` con código `INVALID_TOKEN`.
- Timeout o caída downstream devuelve `503` con `ApiError` y código `SERVICE_UNAVAILABLE`.
- `/actuator/health` responde sin JWT.
- Redis caído no bloquea el acceso (fail-open).

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
   - **Solución**: Asegurar que `JWT_ISSUER` sea idéntico en ambos servicios
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
4. **Token Blacklist - Redis dependency**: Si Redis cae, los tokens revocados no se detectan (fail-open)
   - **Impacto**: Un token revocado por logout puede seguir funcionando hasta su expiración natural
   - **Mitigación**: Monitorear la salud de Redis; considerar TTL corto en tokens

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
