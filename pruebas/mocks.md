# Estrategia de Mocks para API Gateway

Para las pruebas del microservicio API Gateway, se utiliza una estrategia basada en mocks para simular las comunicaciones con servicios externos y validar el funcionamiento del gateway en aislamiento.

## Mocks principales

### Mock JWT
- Uso de tokens JWT estáticos válidos para pruebas de happy path.
- Tokens inválidos o expirados para validar rechazo.
- Se puede utilizar librerías de generación de JWT o tokens precalculados para pruebas.

### Mock servicios backend
- Servicios backend simulados mediante interceptores HTTP o servidores mock que respondan a las rutas proxied.
- Respuestas para flujos felices y errores, incluyendo códigos HTTP, cuerpos JSON, headers propagados.
- Simulación de estados de servicios caídos (500, 503) para probar circuit breaker y fallback.

### Mocks de infraestructura
- Redis y Kafka se omiten o mockean si es necesario, pues el gateway no realiza operaciones directas sobre ellos en flujos funcionales.
- La infraestructura de descubrimiento (Eureka) está deshabilitada en pruebas, evitando dependencias externas.

## Implementación recomendada

- Para pruebas automatizadas, usar WebTestClient con mocks HTTP configurados en contexto de pruebas.
- Para pruebas manuales, desplegar mocks HTTP externos o usar herramientas como WireMock.
- Mantener los mocks actualizados en función de los contratos OpenAPI definidos y la matriz de casos de prueba.

## Casos especiales

- Simulación de delays para tiempos de timeout y circuit breaker.
- Respuesta con headers personalizados para validar propagación en el gateway.
- Validación de CORS y preflight con configuraciones simuladas.

---
