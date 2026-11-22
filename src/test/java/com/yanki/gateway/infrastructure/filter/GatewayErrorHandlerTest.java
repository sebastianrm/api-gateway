package com.yanki.gateway.infrastructure.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;

import reactor.test.StepVerifier;

import com.yanki.gateway.infrastructure.support.ErrorResponseWriter;

/**
 * Pruebas unitarias de {@link GatewayErrorHandler}.
 */
class GatewayErrorHandlerTest {

    private GatewayErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        errorHandler = new GatewayErrorHandler(new ErrorResponseWriter(objectMapper));
    }

    @Test
    @DisplayName("Responde 503 SERVICE_UNAVAILABLE cuando el servicio no esta disponible")
    void shouldReturnServiceUnavailableForUnavailableService() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange, new ServiceUnavailableException("Servicio caido")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(body)
                .contains("\"status\":503")
                .contains("\"error\":\"Service Unavailable\"")
                .contains("\"code\":\"SERVICE_UNAVAILABLE\"")
                .contains("\"message\":\"Servicio caido\"")
                .contains("\"path\":\"/api/accounts/123\"");
    }

    @Test
    @DisplayName("Responde 503 SERVICE_UNAVAILABLE cuando la conexion al downstream es rechazada")
    void shouldReturnServiceUnavailableForConnectException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange, new ConnectException("Connection refused")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(body)
                .contains("\"code\":\"SERVICE_UNAVAILABLE\"")
                .contains("\"message\":\"Connection refused\"")
                .contains("ConnectException: Connection refused");
    }

    @Test
    @DisplayName("Responde 503 SERVICE_UNAVAILABLE cuando el circuit breaker esta abierto")
    void shouldReturnServiceUnavailableForOpenCircuitBreaker() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));
        CallNotPermittedException exception =
                CallNotPermittedException.createCallNotPermittedException(
                        io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults("accounts-service"));

        StepVerifier.create(errorHandler.handle(exchange, exception)).verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(body).contains("\"code\":\"SERVICE_UNAVAILABLE\"");
    }

    @Test
    @DisplayName("Responde 504 GATEWAY_TIMEOUT cuando el downstream excede el timeout")
    void shouldReturnGatewayTimeoutForTimeoutException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange, new TimeoutException("Operation timed out")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(body)
                .contains("\"status\":504")
                .contains("\"error\":\"Gateway Timeout\"")
                .contains("\"code\":\"GATEWAY_TIMEOUT\"")
                .contains("\"message\":\"Operation timed out\"");
    }

    @Test
    @DisplayName("Responde el status de ResponseStatusException y usa el nombre de la excepcion como code")
    void shouldReturnResolvedStatusForResponseStatusException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange,
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud invalida")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body)
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"code\":\"RESPONSESTATUSEXCEPTION\"")
                .contains("\"message\":\"400 BAD_REQUEST \\\"Solicitud invalida\\\"\"");
    }

    @Test
    @DisplayName("Responde 500 INTERNAL_ERROR y mensaje por defecto cuando la excepcion no tiene mensaje")
    void shouldReturnInternalErrorWithDefaultMessageWhenExceptionMessageIsBlank() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange, new RuntimeException(" "))).verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(body)
                .contains("\"status\":500")
                .contains("\"error\":\"Internal Server Error\"")
                .contains("\"code\":\"INTERNAL_ERROR\"")
                .contains("\"message\":\"El servicio destino no esta disponible en este momento\"")
                .contains("\"details\":[]");
    }

    @Test
    @DisplayName("Incluye el detalle de la causa raiz cuando existe una excepcion anidada")
    void shouldIncludeRootCauseDetail() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));
        RuntimeException exception = new RuntimeException("Fallo superior", new IllegalStateException("Causa raiz"));

        StepVerifier.create(errorHandler.handle(exchange, exception)).verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(body)
                .contains("\"code\":\"INTERNAL_ERROR\"")
                .contains("\"message\":\"Fallo superior\"")
                .contains("IllegalStateException: Causa raiz");
    }

    @Test
    @DisplayName("Responde 405 METHOD_NOT_ALLOWED cuando se invoca un metodo HTTP no permitido")
    void shouldReturnMethodNotAllowedFor405ResponseStatusException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/accounts/123"));

        StepVerifier.create(errorHandler.handle(exchange,
                new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(body)
                .contains("\"status\":405")
                .contains("\"error\":\"Method Not Allowed\"")
                .contains("\"code\":\"METHOD_NOT_ALLOWED\"")
                .contains("\"message\":\"405 METHOD_NOT_ALLOWED \\\"Method Not Allowed\\\"\"");
    }

    @Test
    @DisplayName("Responde 404 NOT_FOUND cuando ResponseStatusException indica recurso no encontrado")
    void shouldReturnNotFoundFor404ResponseStatusException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/reports/customer-360/123"));

        StepVerifier.create(errorHandler.handle(exchange,
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No static resource api/reports/customer-360.")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body)
                .contains("\"status\":404")
                .contains("\"error\":\"Not Found\"")
                .contains("\"code\":\"NOT_FOUND\"")
                .contains("\"message\":\"404 NOT_FOUND \\\"No static resource api/reports/customer-360.\\\"\"");
    }

    @Test
    @DisplayName("Responde 404 NOT_FOUND cuando NoResourceFoundException de WebFlux indica recurso no encontrado")
    void shouldReturnNotFoundForWebFluxNoResourceFoundException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/reports/customer-360/123"));

        StepVerifier.create(errorHandler.handle(exchange,
                new org.springframework.web.reactive.resource.NoResourceFoundException(
                        "No static resource api/reports/customer-360.")))
                .verifyComplete();

        String body = exchange.getResponse().getBodyAsString().block();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body)
                .contains("\"status\":404")
                .contains("\"error\":\"Not Found\"")
                .contains("\"code\":\"NOT_FOUND\"");
    }
}
