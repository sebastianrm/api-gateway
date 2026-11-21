package com.yanki.gateway.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rutas publicas de la API Gateway, es decir, aquellas que NO exigen token JWT.
 *
 * <p>Los patrones se evaluan con la sintaxis Ant ({@code /api/auth/**}). El
 * listado se externaliza en {@code application.yml} con la clave
 * {@code yanki.security.public-paths}.</p>
 */
@ConfigurationProperties(prefix = "yanki.security")
public class GatewayPublicPathProperties {

    /**
     * Patrones Ant de las rutas accesibles sin token JWT.
     */
    private List<String> publicPaths = new ArrayList<>();

    /**
     * Devuelve los patrones Ant de las rutas publicas.
     *
     * @return patrones de rutas publicas
     */
    public List<String> getPublicPaths() {
        return publicPaths;
    }

    /**
     * Define los patrones Ant de las rutas publicas.
     *
     * @param publicPaths patrones de rutas publicas
     */
    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths == null ? new ArrayList<>() : new ArrayList<>(publicPaths);
    }
}
