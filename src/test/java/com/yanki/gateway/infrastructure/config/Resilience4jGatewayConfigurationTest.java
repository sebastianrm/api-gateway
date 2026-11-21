package com.yanki.gateway.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que la configuracion de Resilience4j declarada en {@code application.yml}
 * llega efectivamente a los registros usados por el filtro {@code CircuitBreaker} de
 * Spring Cloud Gateway.
 *
 * <p>El timeout del circuit breaker reactivo NO se configura con
 * {@code spring.cloud.gateway.filter.circuit-breaker.*} (esa familia solo admite
 * {@code fallbackUri} y {@code statusCodes}) ni con
 * {@code spring.cloud.circuitbreaker.resilience4j.configs.*} (propiedad que ya no
 * existe en Spring Cloud CircuitBreaker 3.1.x). La property que realmente surte
 * efecto es {@code resilience4j.timelimiter.configs.default.timeoutDuration},
 * porque el filtro crea el breaker con id {@code default} y la factoria reactiva
 * busca la configuracion con ese mismo nombre en el {@code TimeLimiterRegistry}.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class Resilience4jGatewayConfigurationTest {

    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    @DisplayName("El TimeLimiter 'default' aplica el timeout de 2 segundos")
    void shouldApplyTwoSecondsTimeoutToDefaultTimeLimiter() {
        TimeLimiterConfig config = timeLimiterRegistry.getConfiguration("default").orElseThrow();

        assertThat(config.getTimeoutDuration()).isEqualTo(Duration.ofSeconds(2));
        assertThat(timeLimiterRegistry.getDefaultConfig().getTimeoutDuration()).isEqualTo(Duration.ofSeconds(2));
    }

    @Test
    @DisplayName("El circuit breaker 'default' aplica la ventana deslizante y los umbrales configurados")
    void shouldApplyCircuitBreakerSettingsToDefaultBreaker() {
        CircuitBreakerConfig config = circuitBreakerRegistry.getConfiguration("default").orElseThrow();

        assertThat(config.getSlidingWindowSize()).isEqualTo(10);
        assertThat(config.getFailureRateThreshold()).isEqualTo(50.0f);
        assertThat(config.getWaitIntervalFunctionInOpenState().apply(1)).isEqualTo(10_000L);
        assertThat(config.getMinimumNumberOfCalls()).isEqualTo(5);
    }
}
