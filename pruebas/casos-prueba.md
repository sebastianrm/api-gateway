# Casos de Prueba para API Gateway

## CP-047 — Enrutamiento exitoso y propagación de identidad
- **Tipo**: Happy Path
- **Precondiciones**:
  - Gateway operativo.
  - JWT válido.
  - Servicio destino disponible.
- **Pasos**:
  1. Invocar endpoint a través del gateway.
  2. Validar routing correcto.
  3. Validar propagación de headers de identidad/autorización.
- **Postcondiciones**:
  - Solicitud atendida por el servicio destino.
- **Errores esperados**: No aplica.

## CP-048 — Rechazo por token inválido o servicio downstream no disponible
- **Tipo**: Error
- **Precondiciones**:
  - JWT inválido o servicio destino fuera de línea.
- **Pasos**:
  1. Invocar endpoint vía gateway.
  2. Validar respuesta de seguridad o fallback.
- **Postcondiciones**:
  - No se procesa la solicitud de negocio.
- **Errores esperados**:
  - HTTP `401`, `403`, `502`, `503` o timeout controlado.

# Mocks Necesarios
- **Servicio Mock JWT**: Para generar tokens de prueba.
- **Interceptores de Servicio Mock**: Para simular respuestas de servicios externos.
