package com.yanki.gateway.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.yanki.gateway.domain.model.JwtPrincipal;
import com.yanki.gateway.infrastructure.cache.TokenBlacklistService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Validador de tokens JWT (HS256) de la plataforma Yanki.
 *
 * <p>Clase testeable de forma unitaria: no depende de Spring WebFlux ni de
 * ningun bean de infraestructura, solo de {@link GatewaySecurityProperties}.</p>
 *
 * <p>Reglas de validacion:</p>
 * <ul>
 *     <li>La firma debe ser HMAC-SHA256 con el secreto configurado.</li>
 *     <li>El claim {@code iss} debe coincidir con el emisor configurado.</li>
 *     <li>El token debe traer claim {@code exp} (caducidad) y no puede estar expirado.</li>
 *     <li>La ventana de validez ({@code exp - iat}) no puede superar la expiracion
 *     configurada mas la tolerancia Grace.</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtTokenValidator {

    /**
     * Algoritmo de firma exigido (HS256) y nombre JCA de la clave simetrica.
     */
    public static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Longitud minima del secreto en bytes para HS256 (256 bits).
     */
    public static final int MIN_SECRET_LENGTH_BYTES = 32;

    private static final String ROLES_CLAIM = "roles";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String SCOPE_CLAIM = "scope";
    private static final String USERNAME_CLAIM = "username";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    private static final String USER_ID_CLAIM = "userId";
    private static final String CUSTOMER_ID_CLAIM = "customerId";

    private final GatewaySecurityProperties properties;
    private final SecretKey signingKey;
    private final Duration maxTokenLifetime;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Crea el validador a partir de la configuracion de seguridad.
     *
     * @param properties parametros de seguridad JWT externalizados
     * @param tokenBlacklistService servicio de consulta de lista negra de tokens
     */
    public JwtTokenValidator(GatewaySecurityProperties properties, TokenBlacklistService tokenBlacklistService) {
        this.properties = properties;
        this.signingKey = buildSigningKey(properties.getSecret());
        this.maxTokenLifetime = properties.getExpiration().plus(properties.getMaxLifetimeGrace());
        this.tokenBlacklistService = tokenBlacklistService;
        log.info("JwtTokenValidator inicializado con soporte de lista negra de tokens");
    }

    /**
     * Valida un token JWT y extrae la identidad autenticada.
     *
     * <p>Metodo sincrono que no verifica la lista negra. Usar {@link #validateReactive(String)}
     * para la validacion completa con verificacion de revocacion.</p>
     *
     * @param token token compacto recibido en la cabecera {@code Authorization}
     * @return la identidad del usuario si el token es valido, {@link Optional#empty()} si no lo es
     */
    public Optional<JwtPrincipal> validate(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return toPrincipal(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token JWT rechazado: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Excepcion lanzada cuando un token JWT no es valido o esta revocado.
     *
     * <p>Esta excepcion es capturada por {@code JwtAuthenticationGlobalFilter}
     * para construir la respuesta 401 sin usar Mono.empty(), lo que evita
     * problemas de tipos con operadores reactivos como switchIfEmpty.</p>
     */
    public static class InvalidTokenException extends RuntimeException {
        InvalidTokenException(String message) {
            super(message);
        }
    }

    /**
     * Valida un token JWT de forma reactiva, verificando la firma, los claims
     * y la lista negra de tokens revocados en Redis.
     *
     * <p>Este metodo es la version reactiva de {@link #validate(String)} que ademas
     * consulta la lista negra de Redis para rechazar tokens que fueron revocados
     * por un logout. Si Redis no esta disponible, la verificacion de la lista negra
     * falla de forma open (no bloquea el acceso).</p>
     *
     * <p>En lugar de retornar {@link Mono#empty()} para tokens invalidos,
     * lanza {@link InvalidTokenException} para permitir un manejo correcto
     * en flujos reactivos sin ambiguedad de tipos.</p>
     *
     * @param token token compacto recibido en la cabecera {@code Authorization}
     * @return mono con la identidad del usuario si el token es valido y no esta revocado
     * @throws InvalidTokenException si el token es invalido, expirado o revocado
     */
    public Mono<JwtPrincipal> validateReactive(String token) {
        log.debug("[VALIDATOR] Iniciando validacion reactiva del token JWT");

        if (!StringUtils.hasText(token)) {
            log.warn("[VALIDATOR] Token vacio recibido en validacion reactiva");
            return Mono.error(new InvalidTokenException("Token vacio"));
        }

        Claims claims;
        try {
            log.debug("[VALIDATOR] Verificando firma y claims del token JWT");
            claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.debug("[VALIDATOR] Firma y claims del token JWT validados correctamente");
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("[VALIDATOR] Token JWT rechazado por firma o claims invalidos: {}", ex.getMessage());
            return Mono.error(new InvalidTokenException("Firma o claims invalidos: " + ex.getMessage()));
        }

        Optional<JwtPrincipal> principal = toPrincipal(claims);
        if (principal.isEmpty()) {
            log.warn("[VALIDATOR] Token JWT rechazado: no se pudo extraer el principal");
            return Mono.error(new InvalidTokenException("No se pudo extraer el principal"));
        }

        // Verificar si el token esta en la lista negra (flujo reactivo puro)
        log.debug("[VALIDATOR] Verificando lista negra de tokens");

        return tokenBlacklistService.isBlacklisted(token)
                .doOnNext(isBl -> log.debug("[VALIDATOR] Valor recibido de isBlacklisted: {}", isBl))
                .defaultIfEmpty(false)
                .flatMap(isBlacklisted -> {
                    log.debug("[VALIDATOR] Procesando resultado de lista negra: {}", isBlacklisted);
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("[VALIDATOR] Token JWT rechazado: el token esta en la lista negra (revocado por logout)");
                        return Mono.error(new InvalidTokenException("Token revocado (en lista negra)"));
                    }
                    log.debug("[VALIDATOR] Token JWT validado exitosamente");
                    return Mono.just(principal.get());
                })
                .doOnNext(p -> log.debug("[VALIDATOR] Principal emitido correctamente"))
                .doOnSuccess(p -> log.debug("[VALIDATOR] Flujo validateReactive() completado exitosamente"));
    }

    /**
     * Extrae la identidad autenticada de los claims ya validados.
     *
     * @param claims claims del token verificado
     * @return la identidad del usuario, vacia si el token no cumple las politicas de la Gateway
     */
    private Optional<JwtPrincipal> toPrincipal(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration == null) {
            log.debug("Token JWT rechazado: no contiene el claim 'exp'");
            return Optional.empty();
        }
        if (isLifetimeTooLong(claims, expiration)) {
            return Optional.empty();
        }
        String userId = resolveUserId(claims);
        String username = resolveUsername(claims, userId);
        String customerId = resolveCustomerId(claims);
        return Optional.of(new JwtPrincipal(userId, username, customerId, resolveRoles(claims)));
    }

    /**
     * Verifica que la ventana de validez del token no exceda la expiracion configurada.
     *
     * @param claims claims del token verificado
     * @param expiration claim {@code exp} del token
     * @return {@code true} si el token vive mas de lo permitido
     */
    private boolean isLifetimeTooLong(Claims claims, Date expiration) {
        Date issuedAt = claims.getIssuedAt();
        if (issuedAt == null) {
            return false;
        }
        long lifetimeSeconds = (expiration.getTime() - issuedAt.getTime()) / 1000L;
        if (lifetimeSeconds <= maxTokenLifetime.getSeconds()) {
            return false;
        }
        log.debug("Token JWT rechazado: vida maxima excedida ({} s, maximo {} s)",
                lifetimeSeconds, maxTokenLifetime.getSeconds());
        return true;
    }

    /**
     * Resuelve el identificador del usuario autenticado.
     *
     * @param claims claims del token verificado
     * @return identificador del usuario
     */
    private String resolveUserId(Claims claims) {
        String subject = claims.getSubject();
        if (StringUtils.hasText(subject)) {
            return subject;
        }
        Object userId = claims.get(USER_ID_CLAIM);
        return userId == null ? "" : String.valueOf(userId);
    }

    /**
     * Resuelve el nombre de usuario autenticado.
     *
     * @param claims claims del token verificado
     * @param userId identificador del usuario ya resuelto
     * @return nombre de usuario
     */
    private String resolveUsername(Claims claims, String userId) {
        Object username = claims.get(USERNAME_CLAIM);
        if (username != null && StringUtils.hasText(String.valueOf(username))) {
            return String.valueOf(username);
        }
        Object preferredUsername = claims.get(PREFERRED_USERNAME_CLAIM);
        if (preferredUsername != null && StringUtils.hasText(String.valueOf(preferredUsername))) {
            return String.valueOf(preferredUsername);
        }
        return userId;
    }

    /**
     * Resuelve el identificador del cliente autenticado.
     *
     * @param claims claims del token verificado
     * @return identificador del cliente
     */
    private String resolveCustomerId(Claims claims) {
        Object customerId = claims.get(CUSTOMER_ID_CLAIM);
        return customerId == null ? "" : String.valueOf(customerId);
    }

    /**
     * Resuelve los roles del usuario autenticado.
     *
     * <p>Se admite el claim {@code roles} (array JSON o texto separado por comas),
     * {@code authorities} y, como ultimo recurso, {@code scope}.</p>
     *
     * @param claims claims del token verificado
     * @return roles del usuario
     */
    private List<String> resolveRoles(Claims claims) {
        List<String> roles = readRoles(claims.get(ROLES_CLAIM), ",");
        if (roles.isEmpty()) {
            roles = readRoles(claims.get(AUTHORITIES_CLAIM), ",");
        }
        if (roles.isEmpty()) {
            roles = readRoles(claims.get(SCOPE_CLAIM), " ");
        }
        return roles;
    }

    /**
     * Convierte un claim de roles en una lista de roles normalizada.
     *
     * @param claim valor del claim
     * @param separator separador usado cuando el claim es texto plano
     * @return lista de roles, nunca nula
     */
    private List<String> readRoles(Object claim, String separator) {
        List<String> roles = new ArrayList<>();
        if (claim instanceof Collection<?> collection) {
            collection.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(roles::add);
        } else if (claim instanceof String value) {
            for (String role : value.split(separator)) {
                String trimmed = role.trim();
                if (StringUtils.hasText(trimmed)) {
                    roles.add(trimmed);
                }
            }
        }
        return roles;
    }

    /**
     * Construye la clave simetrica HS256 a partir del secreto configurado.
     *
     * @param secret secreto configurado
     * @return clave de firma HS256
     */
    private static SecretKey buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException(
                    "El secreto JWT no puede estar vacio: configure yanki.security.jwt.secret");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException("El secreto JWT debe tener al menos " + MIN_SECRET_LENGTH_BYTES
                    + " bytes para firmar con HS256");
        }
        return new SecretKeySpec(keyBytes, HMAC_SHA256);
    }
}
