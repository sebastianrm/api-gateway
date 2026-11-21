package com.yanki.gateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuracion CORS de la API Gateway a partir de properties externalizables.
 *
 * <p>Se construye un {@link CorsWebFilter} con la configuracion declarada en
 * {@code yanki.gateway.cors.*}, de modo que los preflight (OPTIONS) de todos los
 * servicios se respondan sin exigir token JWT y sin invocar a los destinos.</p>
 */
@Configuration(proxyBeanMethods = false)
/**
 * Configuración CORS para la Gateway.
 *
 * <p>Define los filtros CORS que permiten interacciones HTTP seguras
 * entre dominios distintos.</p>
 *
 * <author> Sebastian Retamal
 * <contact> semorale@emeal.nttdata.com
 * </author>
 * @version 1.0.0
 */
public class GatewayCorsConfiguration {
    private final GatewayCorsProperties properties;

    /**
     * Crea la configuracion CORS.
     *
     * @param properties parametros CORS externalizados
     */
    public GatewayCorsConfiguration(GatewayCorsProperties properties) {
        this.properties = properties;
    }

    /**
     * Crea el filtro CORS reactivo de la Gateway.
     *
     * @return filtro CORS registrado en la cadena reactiva
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(properties.getAllowedOrigins());
        corsConfiguration.setAllowedMethods(properties.getAllowedMethods());
        corsConfiguration.setAllowedHeaders(properties.getAllowedHeaders());
        corsConfiguration.setExposedHeaders(properties.getExposedHeaders());
        corsConfiguration.setAllowCredentials(properties.isAllowCredentials());
        corsConfiguration.setMaxAge(properties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
