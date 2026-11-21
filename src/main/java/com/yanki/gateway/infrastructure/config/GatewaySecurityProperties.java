package com.yanki.gateway.infrastructure.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Parametros de seguridad JWT de la API Gateway.
 *
 * <p>Todos los valores se externalizan: el secreto, el emisor y la duracion de
 * los tokens NUNCA estan escritos en el codigo, provienen de
 * {@code application.yml}, del Config Server o de variables de entorno.</p>
 */
@Validated
@ConfigurationProperties(prefix = "yanki.security.jwt")
/**
 * Propiedades de seguridad para la Gateway.
 *
 * <p>Configura detalles de seguridad, como los secretos y los tiempos de vida del token.</p>
 *
 * <author> Sebastian Retamal
 * <contact> semorale@emeal.nttdata.com
 * </author>
 * @version 1.0.0
 */
public class GatewaySecurityProperties {
    /**
     * Secreto compartido con auth-service para firmar y verificar tokens HS256.
     * Debe tener al menos 32 bytes.
     */
    @NotBlank
    private String secret;

    /**
     * Emisor (claim {@code iss}) que todos los tokens validos deben declarar.
     */
    @NotBlank
    private String issuer;

    /**
     * Tiempo de vida de los tokens emitidos por auth-service.
     * Se usa como vida maxima admitida por la Gateway (mas la tolerancia Grace).
     */
    @NotNull
    private Duration expiration = Duration.ofHours(1);

    /**
     * Tolerancia aplicada a la vida maxima del token para tolerar desfases de reloj
     * entre auth-service y la Gateway.
     */
    @NotNull
    private Duration maxLifetimeGrace = Duration.ofMinutes(5);

    /**
     * Devuelve el secreto configurado para HS256.
     *
     * @return secreto de firma
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Define el secreto configurado para HS256.
     *
     * @param secret secreto de firma
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Devuelve el emisor exigido en el claim {@code iss}.
     *
     * @return emisor del token
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Define el emisor exigido en el claim {@code iss}.
     *
     * @param issuer emisor del token
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Devuelve el tiempo de vida configurado para los tokens.
     *
     * @return duracion de vida del token
     */
    public Duration getExpiration() {
        return expiration;
    }

    /**
     * Define el tiempo de vida configurado para los tokens.
     *
     * @param expiration duracion de vida del token
     */
    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }

    /**
     * Devuelve la tolerancia aplicada a la vida maxima del token.
     *
     * @return tolerancia de vida maxima
     */
    public Duration getMaxLifetimeGrace() {
        return maxLifetimeGrace;
    }

    /**
     * Define la tolerancia aplicada a la vida maxima del token.
     *
     * @param maxLifetimeGrace tolerancia de vida maxima
     */
    public void setMaxLifetimeGrace(Duration maxLifetimeGrace) {
        this.maxLifetimeGrace = maxLifetimeGrace;
    }
}
