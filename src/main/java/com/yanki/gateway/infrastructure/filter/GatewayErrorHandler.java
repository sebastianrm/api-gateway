package com.yanki.gateway.infrastructure.filter;

import java.net.ConnectException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.yanki.gateway.infrastructure.support.ErrorResponseWriter;
import com.yanki.gateway.interfaces.rest.dto.ApiError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de errores de la API Gateway.
 *
 * <p>Intercepta los errores que escapan de la cadena de filtros y los traduce a
 * respuestas JSON con el esquema {@code ApiError}. En particular, cuando el
 * microservicio destino no esta disponible (servicio no registrado en Eureka,
 * conexion rechazada, circuit breaker abierto o tiempo de espera agotado) se
 * responde 503 con el mismo esquema que el resto de errores de la Gateway.</p>
 */
@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    /**
     * Codigo de error devuelto cuando el destino no esta disponible.
     */
    public static final String SERVICE_UNAVAILABLE_CODE = "SERVICE_UNAVAILABLE";

    /**
     * Codigo de error devuelto cuando el destino no responde dentro del timeout.
     */
    public static final String GATEWAY_TIMEOUT_CODE = "GATEWAY_TIMEOUT";

    /**
     * Codigo de error devuelto para fallos no controlados.
     */
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";

    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        ApiError error = ApiError.of(Instant.now(), status.value(), status.getReasonPhrase(), resolveCode(status, ex),
                resolveMessage(ex), exchange.getRequest().getPath().value(), resolveDetails(ex));
        if (status.is5xxServerError()) {
            log.error("Error en la Gateway para {}: {}", exchange.getRequest().getPath().value(), ex.getMessage(), ex);
        } else {
            log.warn("Error en la Gateway para {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        }
        return errorResponseWriter.write(exchange, status, error);
    }

    /**
     * Traduce la excepcion al codigo HTTP que corresponde.
     *
     * @param ex excepcion capturada
     * @return codigo HTTP de la respuesta
     */
    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof ServiceUnavailableException || ex instanceof CallNotPermittedException
                || ex instanceof ConnectException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        if (ex instanceof TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        if (ex instanceof ResponseStatusException responseStatusException) {
            HttpStatus status = HttpStatus.resolve(responseStatusException.getStatusCode().value());
            return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Traduce la excepcion al codigo de error estable de la plataforma.
     *
     * @param status codigo HTTP resuelto
     * @param ex excepcion capturada
     * @return codigo de error
     */
    private String resolveCode(HttpStatus status, Throwable ex) {
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return SERVICE_UNAVAILABLE_CODE;
        }
        if (status == HttpStatus.GATEWAY_TIMEOUT) {
            return GATEWAY_TIMEOUT_CODE;
        }
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return INTERNAL_ERROR_CODE;
        }
        return ex.getClass().getSimpleName().toUpperCase(Locale.ROOT);
    }

    /**
     * Traduce la excepcion a un mensaje legible.
     *
     * @param ex excepcion capturada
     * @return mensaje de error
     */
    private String resolveMessage(Throwable ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return "El servicio destino no esta disponible en este momento";
        }
        return message;
    }

    /**
     * Extrae los detalles adicionales de la causa raiz de la excepcion.
     *
     * @param ex excepcion capturada
     * @return lista de detalles, vacia si no hay informacion adicional
     */
    private List<String> resolveDetails(Throwable ex) {
        Throwable rootCause = rootCause(ex);
        if (rootCause.getMessage() == null || rootCause.getMessage().isBlank()) {
            return List.of();
        }
        return List.of(rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage());
    }

    /**
     * Busca la causa raiz de la excepcion.
     *
     * @param ex excepcion capturada
     * @return causa raiz o la propia excepcion si no hay ninguna
     */
    private Throwable rootCause(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
