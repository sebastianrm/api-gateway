package com.yanki.gateway.interfaces.rest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.yanki.gateway.interfaces.rest.dto.ApiError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoint de respaldo (fallback) de la API Gateway.
 *
 * <p>Es el destino del {@code fallbackUri} configurado en el filtro
 * {@code CircuitBreaker} de Spring Cloud Gateway: cuando un microservicio no
 * esta disponible, el breaker abre y la peticion se reenvia aqui, que responde
 * 503 con el cuerpo JSON estandar {@link ApiError}.</p>
 *
 * <p>La ruta {@code /fallback/**} es publica: no exige token JWT.</p>
 */
@RestController
@RequestMapping("/fallback")
@Tag(name = "Fallback", description = "Respuesta de respaldo de la API Gateway")
public class FallbackController {

    /**
     * Codigo de error devuelto cuando el destino no esta disponible.
     */
    public static final String SERVICE_UNAVAILABLE_CODE = "SERVICE_UNAVAILABLE";

    /**
     * Mensaje devuelto cuando el destino no esta disponible.
     */
    public static final String SERVICE_UNAVAILABLE_MESSAGE =
            "El servicio destino no esta disponible en este momento, intentelo nuevamente";

    /**
     * Estado del gateway.
     *
     * @return estado basico de la API Gateway
     */
    @GetMapping("/health")
    @Operation(summary = "Consulta el estado del gateway")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "UP");
        payload.put("service", "api-gateway");
        payload.put("timestamp", Instant.now().toString());
        return Mono.just(payload);
    }

    /**
     * Respuesta de respaldo para cualquier peticion que no pudo atenderse.
     *
     * @param exchange intercambio actual
     * @return respuesta 503 con el cuerpo de error estandar
     */
    @RequestMapping("/**")
    @Operation(summary = "Responde 503 cuando el microservicio destino no esta disponible")
    public Mono<ResponseEntity<ApiError>> fallback(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiError error = ApiError.of(Instant.now(), HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), SERVICE_UNAVAILABLE_CODE, SERVICE_UNAVAILABLE_MESSAGE,
                path, null);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error));
    }
}
