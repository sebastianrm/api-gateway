package com.yanki.gateway.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * cuerpo de error estandar de la API Gateway.
 *
 * <p>Formato unico para todos los errores emitidos por la Gateway
 * (autenticacion, circuit breaker y destino no disponible):</p>
 *
 * <pre>
 * {
 *   "timestamp": "2026-11-04T18:30:00Z",
 *   "status": 401,
 *   "error": "Unauthorized",
 *   "code": "INVALID_TOKEN",
 *   "message": "...",
 *   "path": "/api/accounts/123",
 *   "details": ["..."]
 * }
 * </pre>
 *
 * @param timestamp instante de generacion del error
 * @param status codigo HTTP del error
 * @param error descripcion HTTP estandar del error
 * @param code codigo de error estable de la plataforma
 * @param message descripcion legible del error
 * @param path ruta que originó el error, puede ser nula
 * @param details detalles adicionales, puede ser nula
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Instant timestamp, int status, String error, String code, String message, String path,
        List<String> details) {

    /**
     * Crea un error con la ruta y los detalles informados.
     *
     * @param timestamp instante de generacion del error
     * @param status codigo HTTP del error
     * @param error descripcion HTTP estandar del error
     * @param code codigo de error estable de la plataforma
     * @param message descripcion legible del error
     * @param path ruta que originó el error
     * @param details detalles adicionales
     * @return cuerpo de error
     */
    public static ApiError of(Instant timestamp, int status, String error, String code, String message, String path,
            List<String> details) {
        return new ApiError(timestamp, status, error, code, message, path, details);
    }

    /**
     * Crea un error sin ruta ni detalles adicionales.
     *
     * @param timestamp instante de generacion del error
     * @param status codigo HTTP del error
     * @param error descripcion HTTP estandar del error
     * @param code codigo de error estable de la plataforma
     * @param message descripcion legible del error
     * @return cuerpo de error
     */
    public static ApiError of(Instant timestamp, int status, String error, String code, String message) {
        return new ApiError(timestamp, status, error, code, message, null, null);
    }
}
