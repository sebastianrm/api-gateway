# Pruebas para API Gateway

Este directorio reúne los casos de prueba funcionales y la estrategia de mockeo para el microservicio API Gateway de la plataforma Yanki.

## Estructura

- `casos-prueba.md`: Casos de prueba funcionales, cubriendo rutas protegidas, gestión de errores y rutas abiertas.
- `mocks.md`: Estrategia para simular sistemas externos y servicios downstream.

## Alcance de pruebas

- Validación JWT (401 por token ausente o inválido; 200 si es válido y routing correcto)
- Fallback unificado (503 cuando servicio destino está caído)
- Propagación de headers de identidad (`X-User-*`)
- Acceso abierto a `/actuator/**`, `/v3/api-docs` y `/swagger-ui.html`
- CORS configurado correctamente (headers y métodos permitidos)
- Estrategia para mockear servicios backend y tokens JWT

## Mockeo de dependencias

- No requiere conexión real a Eureka, servicios backend ni a Redis o Kafka para simular flujos funcionales.
- La validación JWT se puede realizar con tokens generados por un mock estático.
- Los endpoints de servicios destino pueden ser interceptados y retornan respuestas simuladas.

## Ejecución

Las pruebas pueden automatizarse vía WebTestClient (como en `GatewayHttpFlowIntegrationTest`) o ejecutarse manualmente vía Postman/curl contra una instancia local del gateway en modo standalone.

---
