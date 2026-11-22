package com.yanki.gateway.infrastructure.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion CORS de la API Gateway.
 *
 * <p>Se externaliza con la clave {@code yanki.gateway.cors} para no dejar
 * ningun origen, metodo o cabecera escritos en el codigo.</p>
 */
@ConfigurationProperties(prefix = "yanki.gateway.cors")
/**
 * Propiedades de configuracion CORS de la Gateway.
 *
 * <p>Define los parametros CORS configurables mediante propiedades externalizadas.</p>
 *
 * <author> Sebastian Retamal
 * <contact> semorale@emeal.nttdata.com
 * </author>
 * @version 1.0.0
 */
public class GatewayCorsProperties {
    /**
     * Origenes permitidos, por ejemplo {@code https://app.yanki.pe}.
     */
    private List<String> allowedOrigins = new ArrayList<>();

    /**
     * Metodos HTTP permitidos.
     */
    private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    /**
     * Cabeceras de peticion permitidas.
     */
    private List<String> allowedHeaders = new ArrayList<>(List.of(
            "Authorization", "Content-Type", "Accept", "X-Request-ID", "X-Correlation-ID"
    ));

    /**
     * Cabeceras de respuesta expuestas al navegador.
     */
    private List<String> exposedHeaders = new ArrayList<>();

    /**
     * Indica si se permiten credenciales (cookies, Authorization) cross-origin.
     */
    private boolean allowCredentials = false;

    /**
     * Tiempo maximo de cache de la respuesta de preflight.
     */
    private Duration maxAge = Duration.ofHours(1);

    /**
     * Devuelve los origenes permitidos.
     *
     * @return origenes permitidos
     */
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Define los origenes permitidos.
     *
     * @param allowedOrigins origenes permitidos
     */
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins == null ? new ArrayList<>() : new ArrayList<>(allowedOrigins);
    }

    /**
     * Devuelve los metodos HTTP permitidos.
     *
     * @return metodos permitidos
     */
    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Define los metodos HTTP permitidos.
     *
     * @param allowedMethods metodos permitidos
     */
    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods == null ? new ArrayList<>() : new ArrayList<>(allowedMethods);
    }

    /**
     * Devuelve las cabeceras de peticion permitidas.
     *
     * @return cabeceras permitidas
     */
    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * Define las cabeceras de peticion permitidas.
     *
     * @param allowedHeaders cabeceras permitidas
     */
    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders == null ? new ArrayList<>() : new ArrayList<>(allowedHeaders);
    }

    /**
     * Devuelve las cabeceras de respuesta expuestas.
     *
     * @return cabeceras expuestas
     */
    public List<String> getExposedHeaders() {
        return exposedHeaders;
    }

    /**
     * Define las cabeceras de respuesta expuestas.
     *
     * @param exposedHeaders cabeceras expuestas
     */
    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders == null ? new ArrayList<>() : new ArrayList<>(exposedHeaders);
    }

    /**
     * Indica si se permiten credenciales cross-origin.
     *
     * @return {@code true} si se permiten credenciales
     */
    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    /**
     * Define si se permiten credenciales cross-origin.
     *
     * @param allowCredentials {@code true} si se permiten credenciales
     */
    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    /**
     * Devuelve el tiempo maximo de cache de la respuesta de preflight.
     *
     * @return duracion maxima de cache
     */
    public Duration getMaxAge() {
        return maxAge;
    }

    /**
     * Define el tiempo maximo de cache de la respuesta de preflight.
     *
     * @param maxAge duracion maxima de cache
     */
    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }
}
