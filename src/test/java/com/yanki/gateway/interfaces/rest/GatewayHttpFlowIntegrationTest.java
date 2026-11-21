package com.yanki.gateway.interfaces.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Pruebas de integracion del flujo HTTP de la API Gateway.
 *
 * <p>Verifican de extremo a extremo el filtro global de JWT, el endpoint de
 * respaldo y el cuerpo JSON de error, sin depender de ningun microservicio
 * destino (Eureka esta deshabilitado en el contexto de prueba).</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class GatewayHttpFlowIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Responde 401 MISSING_TOKEN en una ruta protegida sin token")
    void shouldRejectProtectedRouteWithoutToken() {
        webClient.get().uri("/api/accounts/123")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized")
                .jsonPath("$.code").isEqualTo("MISSING_TOKEN")
                .jsonPath("$.message").exists();

        String body = webClient.get().uri("/api/accounts/123")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body)
                .contains("\"timestamp\"")
                .contains("\"status\":401")
                .contains("\"error\":\"Unauthorized\"")
                .contains("\"code\":\"MISSING_TOKEN\"")
                .contains("\"message\"")
                .doesNotContain("\"path\"")
                .doesNotContain("\"details\"");
    }

    @Test
    @DisplayName("Responde 401 INVALID_TOKEN cuando el token no es valido")
    void shouldRejectProtectedRouteWithInvalidToken() {
        webClient.get().uri("/api/accounts/123")
                .header("Authorization", "Bearer token.invalido")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.code").isEqualTo("INVALID_TOKEN");
    }

    @Test
    @DisplayName("Responde 200 en el endpoint de salud del fallback")
    void shouldExposeFallbackHealthEndpoint() {
        webClient.get().uri("/fallback/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("api-gateway");
    }

    @Test
    @DisplayName("Responde 503 con cuerpo JSON cuando el destino no esta disponible")
    void shouldReturnServiceUnavailableWhenDestinationIsDown() {
        String body = webClient.get().uri("/api/auth/login")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).contains("\"status\":503");
        assertThat(body).contains("\"code\":\"SERVICE_UNAVAILABLE\"");
    }

    @Test
    @DisplayName("Responde el preflight CORS segun la configuracion externalizada")
    void shouldAnswerCorsPreflight() {
        webClient.options().uri("/api/accounts/123")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000")
                .expectHeader().valueMatches(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ".*GET.*");
    }

    @Test
    @DisplayName("El actuator es accesible sin token")
    void shouldExposeActuatorWithoutToken() {
        webClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @DisplayName("La documentacion OpenAPI es accesible sin token")
    void shouldExposeOpenApiWithoutToken() {
        webClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk();
    }
}
