package com.yanki.gateway.infrastructure.support;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Escribe respuestas de error de la Gateway en formato JSON.
 *
 * <p>Se usa tanto desde el filtro global de autenticacion como desde el
 * manejador global de errores, de modo que todos los cuerpos de error de la
 * Gateway compartan exactamente el mismo esquema {@code ApiError}.</p>
 */
@Slf4j
@Component
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    /**
     * Crea el escritor de respuestas de error.
     *
     * @param objectMapper mapper usado para serializar el cuerpo de error
     */
    public ErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Escribe el cuerpo de error en la respuesta del intercambio.
     *
     * @param exchange intercambio actual
     * @param status codigo HTTP de la respuesta
     * @param body cuerpo de error ya serializable
     * @param <T> tipo del cuerpo de error
     * @return mono que completa la escritura de la respuesta
     */
    public <T> Mono<Void> write(ServerWebExchange exchange, HttpStatus status, T body) {
        byte[] payload = serialize(body);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(payload);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * Serializa el cuerpo de error a JSON.
     *
     * @param body cuerpo de error
     * @param <T> tipo del cuerpo de error
     * @return representacion JSON del cuerpo de error
     */
    private <T> byte[] serialize(T body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException ex) {
            log.error("No se pudo serializar el cuerpo de error de la Gateway", ex);
            return ("{\"status\":500,\"error\":\"Internal Server Error\",\"code\":\"SERIALIZATION_ERROR\","
                    + "\"message\":\"No se pudo serializar el error\"}").getBytes(StandardCharsets.UTF_8);
        }
    }
}
