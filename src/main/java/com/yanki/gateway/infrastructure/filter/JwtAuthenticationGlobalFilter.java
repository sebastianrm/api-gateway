package com.yanki.gateway.infrastructure.filter;

import java.time.Instant;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.yanki.gateway.domain.model.JwtPrincipal;
import com.yanki.gateway.infrastructure.config.GatewayPublicPathProperties;
import com.yanki.gateway.infrastructure.config.JwtTokenValidator;
import com.yanki.gateway.infrastructure.support.ErrorResponseWriter;
import com.yanki.gateway.interfaces.rest.dto.ApiError;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro global que autentica cada peticion que atraviesa la API Gateway.
 *
 * <p>Comportamiento:</p>
 * <ul>
 *     <li>Las rutas publicas (configuradas en {@code yanki.security.public-paths})
 *     dejan pasar la peticion sin exigir token.</li>
 *     <li>En el resto de rutas se exige la cabecera {@code Authorization: Bearer <token>}.
 *     Si falta, se responde 401 con codigo {@code MISSING_TOKEN}.</li>
 *     <li>Si el token es invalido o esta expirado, se responde 401 con codigo
 *     {@code INVALID_TOKEN}.</li>
 *     <li>Si el token es valido, se propagan al destino las cabeceras
 *     {@code X-User-Id}, {@code X-Username} y {@code X-Roles} (roles separados por coma).
 *     Las cabeceras entrantes con esos nombres se descartan para evitar suplantaciones.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    /**
     * Cabecera propagada al destino con el identificador del usuario autenticado.
     */
    public static final String USER_ID_HEADER = "X-User-Id";

    /**
     * Cabecera propagada al destino con el nombre de usuario autenticado.
     */
    public static final String USERNAME_HEADER = "X-Username";

    /**
     * Cabecera propagada al destino con los roles del usuario separados por coma.
     */
    public static final String ROLES_HEADER = "X-Roles";

    /**
     * Cabecera propagada al destino con el ID del cliente autenticado.
     * Se utiliza para que los microservicios downstream validen la propiedad de recursos.
     */
    public static final String CUSTOMER_ID_HEADER = "X-Customer-Id";

    /**
     * Codigo de error devuelto cuando falta la cabecera Authorization.
     */
    public static final String MISSING_TOKEN_CODE = "MISSING_TOKEN";

    /**
     * Codigo de error devuelto cuando el token es invalido o esta expirado.
     */
    public static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenValidator tokenValidator;
    private final GatewayPublicPathProperties publicPathProperties;
    private final ErrorResponseWriter errorResponseWriter;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !isBearer(authorization)) {
            return unauthorized(exchange, MISSING_TOKEN_CODE,
                    "Falta la cabecera Authorization con el token Bearer en las rutas protegidas");
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange, MISSING_TOKEN_CODE,
                    "Falta la cabecera Authorization con el token Bearer en las rutas protegidas");
        }

        log.debug("[FILTER] Validando token JWT para: {}", path);

        return tokenValidator.validateReactive(token)
                .doOnNext(principal -> log.debug("[FILTER] Token validado para: {}", principal.username()))
                .flatMap(principal -> {
                    log.debug("[FILTER] Propagando peticion autenticada para: {}", principal.username());
                    return chain.filter(withPrincipal(exchange, principal));
                })
                .doOnSuccess(v -> log.debug("[FILTER] Peticion completada exitosamente"))
                .doOnTerminate(() -> log.debug("[FILTER] Flujo de validacion terminado"))
                .doFinally(signal -> log.debug("[FILTER] Flujo de validacion finalizado con senal: {}", signal))
                .onErrorResume(com.yanki.gateway.infrastructure.config.JwtTokenValidator.InvalidTokenException.class, ex -> {
                    log.warn("[FILTER] Token JWT no valido o revocado, rechazando peticion a: {} — razon: {}",
                            exchange.getRequest().getPath().value(), ex.getMessage());
                    return unauthorized(exchange, INVALID_TOKEN_CODE,
                            "El token JWT es invalido, ha expirado, no cumple la politica de seguridad o ha sido revocado");
                });
    }

    /**
     * Orden de ejecucion del filtro de autenticacion JWT.
     *
     * <p>Se ejecuta despues de NettyWriteResponseFilter (-1) para que la respuesta de error
     * sea completada por la cadena, y antes que los filtros de ruta y de routing.</p>
     */
    private static final int FILTER_ORDER = 0;

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }

    /**
     * Indica si la cabecera Authorization usa el esquema Bearer (case-insensitive, RFC 7235).
     *
     * @param authorization valor de la cabecera Authorization
     * @return {@code true} si la cabecera usa el esquema Bearer
     */
    private boolean isBearer(String authorization) {
        return authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length());
    }

    /**
     * Indica si la ruta es accesible sin token JWT.
     *
     * @param path ruta de la peticion
     * @return {@code true} si la ruta es publica
     */
    private boolean isPublicPath(String path) {
        for (String pattern : publicPathProperties.getPublicPaths()) {
            if (StringUtils.hasText(pattern) && pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Añade al intercambio las cabeceras de identidad derivadas del token validado.
     *
     * @param exchange intercambio actual
     * @param principal identidad autenticada
     * @return intercambio con las cabeceras de identidad
     */
    private ServerWebExchange withPrincipal(ServerWebExchange exchange, JwtPrincipal principal) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USERNAME_HEADER);
                    headers.remove(ROLES_HEADER);
                    headers.remove(CUSTOMER_ID_HEADER);
                    headers.set(USER_ID_HEADER, principal.userId());
                    headers.set(USERNAME_HEADER, principal.username());
                    headers.set(ROLES_HEADER, principal.rolesAsHeaderValue());
                    headers.set(CUSTOMER_ID_HEADER, principal.customerId());
                })
                .build();
        return exchange.mutate().request(request).build();
    }

    /**
     * Construye la respuesta 401 con el cuerpo {@code ApiError} estandar.
     *
     * @param exchange intercambio actual
     * @param code codigo de error estable
     * @param message descripcion legible del error
     * @return mono que escribe la respuesta de error
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String code, String message) {
        log.debug("Rechazando peticion a {}: {}", exchange.getRequest().getPath().value(), code);
        ApiError error = ApiError.of(Instant.now(), HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), code, message);
        return errorResponseWriter.write(exchange, HttpStatus.UNAUTHORIZED, error);
    }
}
