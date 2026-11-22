package com.yanki.gateway.infrastructure.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.yanki.gateway.domain.model.JwtPrincipal;
import com.yanki.gateway.infrastructure.config.GatewayPublicPathProperties;
import com.yanki.gateway.infrastructure.config.JwtTokenValidator;
import com.yanki.gateway.infrastructure.support.ErrorResponseWriter;

/**
 * Pruebas unitarias de {@link JwtAuthenticationGlobalFilter}.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationGlobalFilterTest {

    @Mock
    private JwtTokenValidator tokenValidator;

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationGlobalFilter filter;

    @BeforeEach
    void setUp() {
        GatewayPublicPathProperties publicPathProperties = new GatewayPublicPathProperties();
        publicPathProperties.setPublicPaths(List.of("/api/auth/**", "/auth/**", "/actuator/**", "/v3/api-docs/**",
                "/swagger-ui/**", "/swagger-ui.html", "/fallback/**"));
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationGlobalFilter(tokenValidator, publicPathProperties,
                new ErrorResponseWriter(objectMapper));
        lenient().when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Deja pasar una ruta publica sin token")
    void shouldAllowPublicPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/auth/login"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(any(ServerWebExchange.class));
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    @DisplayName("Responde 401 MISSING_TOKEN en una ruta protegida sin token")
    void shouldRejectProtectedPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/123"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exchange.getResponse().getBodyAsString().block())
                .contains("\"status\":401")
                .contains("\"error\":\"Unauthorized\"")
                .contains("\"code\":\"MISSING_TOKEN\"");
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Responde 401 MISSING_TOKEN cuando la cabecera Authorization no usa el esquema Bearer")
    void shouldRejectProtectedPathWithMalformedAuthorizationHeader() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/accounts/123").header(HttpHeaders.AUTHORIZATION, "Basic abc123"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exchange.getResponse().getBodyAsString().block()).contains("\"code\":\"MISSING_TOKEN\"");
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("Inyecta X-User-Id, X-Username, X-Customer-Id y X-Roles cuando el token es valido")
    void shouldPropagateIdentityHeadersWhenTokenIsValid() {
        when(tokenValidator.validateReactive("token-valido")).thenReturn(Mono.just(
                new JwtPrincipal("user-123", "juan.perez", "CUST-001", List.of("ROLE_ADMIN", "ROLE_USER"))));
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/accounts/123")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-valido")
                        .header(JwtAuthenticationGlobalFilter.USER_ID_HEADER, "usuario-suplantado"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(captor.capture());
        HttpHeaders headers = captor.getValue().getRequest().getHeaders();
        assertThat(headers.getFirst(JwtAuthenticationGlobalFilter.USER_ID_HEADER)).isEqualTo("user-123");
        assertThat(headers.getFirst(JwtAuthenticationGlobalFilter.USERNAME_HEADER)).isEqualTo("juan.perez");
        assertThat(headers.getFirst(JwtAuthenticationGlobalFilter.CUSTOMER_ID_HEADER)).isEqualTo("CUST-001");
        assertThat(headers.getFirst(JwtAuthenticationGlobalFilter.ROLES_HEADER)).isEqualTo("ROLE_ADMIN,ROLE_USER");
    }

    @Test
    @DisplayName("Responde 401 INVALID_TOKEN cuando el token no es valido")
    void shouldRejectInvalidToken() {
        when(tokenValidator.validateReactive("token-invalido")).thenReturn(Mono.empty());
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/accounts/123")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exchange.getResponse().getBodyAsString().block())
                .contains("\"status\":401")
                .contains("\"error\":\"Unauthorized\"")
                .contains("\"code\":\"INVALID_TOKEN\"");
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }
}
