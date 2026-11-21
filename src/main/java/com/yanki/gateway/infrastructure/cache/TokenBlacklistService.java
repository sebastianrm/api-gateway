package com.yanki.gateway.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Servicio de consulta de lista negra de tokens JWT sobre Redis.
 *
 * <p>Verifica si un token JWT ha sido revocado consultando la lista negra
 * almacenada en Redis por el auth-service durante el logout. Si el token
 * se encuentra en la lista negra, significa que fue revocado y debe ser
 * rechazado por el API Gateway.</p>
 *
 * <p>La clave de Redis sigue el patron {@code yanki:auth:blacklist:<token>}
 * establecido por el auth-service.</p>
 *
 * <p>Los fallos de Redis se manejan de forma graceful: si Redis no esta
 * disponible, se asume que el token NO esta en la lista negra para evitar
 * bloquear el acceso de los usuarios por un fallo de infraestructura.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    /** Prefijo de las claves de lista negra de tokens en Redis. */
    private static final String BLACKLIST_KEY_PREFIX = "yanki:auth:blacklist:";

    /** Plantilla reactiva de Redis con valores de tipo texto. */
    private final ReactiveStringRedisTemplate redisTemplate;

    /**
     * Verifica si un token JWT esta presente en la lista negra de Redis.
     *
     * <p>Si Redis no esta disponible o ocurre un error, se devuelve {@code false}
     * para evitar bloquear el acceso del usuario por un fallo de infraestructura.
     * Esta es una decision de diseno: es preferible permitir un token revocado
     * que bloquear a un usuario valido por un fallo de Redis.</p>
     *
     * @param token el token JWT completo a verificar
     * @return mono con {@code true} si el token esta revocado, {@code false} en caso contrario
     */
    public Mono<Boolean> isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            log.warn("[BLACKLIST] Token nulo o vacio recibido para verificar en la lista negra");
            return Mono.just(false);
        }

        String key = BLACKLIST_KEY_PREFIX + token;
        log.info("[BLACKLIST] Consultando lista negra en Redis para la clave: {}...", key.substring(0, Math.min(40, key.length())));

        return redisTemplate.hasKey(key)
                .doOnSubscribe(s -> log.info("[BLACKLIST] Suscripcion a hasKey() establecida para: {}...", key.substring(0, Math.min(40, key.length()))))
                .doOnNext(exists -> log.info("[BLACKLIST] hasKey() retorno valor: {} para clave: {}...", exists, key.substring(0, Math.min(40, key.length()))))
                .doOnError(err -> log.error("[BLACKLIST] Error en hasKey() para clave: {} — Error: {}", key.substring(0, Math.min(40, key.length())), err.getMessage()))
                .map(exists -> {
                    boolean isBlacklisted = Boolean.TRUE.equals(exists);
                    if (isBlacklisted) {
                        log.info("[BLACKLIST] Token encontrado en la lista negra - el token ha sido revocado");
                    } else {
                        log.info("[BLACKLIST] Token NO encontrado en la lista negra — token valido para continuar");
                    }
                    return isBlacklisted;
                })
                .doOnNext(result -> log.info("[BLACKLIST] Despues de map() — resultado final: {}", result))
                .defaultIfEmpty(false)
                .doOnNext(afterDefault -> log.info("[BLACKLIST] Despues de defaultIfEmpty() — valor emitido: {}", afterDefault))
                .onErrorResume(error -> {
                    log.warn("[BLACKLIST] Error al consultar la lista negra en Redis: {}. "
                            + "Se asume que el token NO esta revocado (fail-open).",
                            error.getMessage());
                    // Fail-open: si Redis falla, no bloqueamos el acceso
                    return Mono.just(false);
                })
                .doOnTerminate(() -> log.info("[BLACKLIST] Flujo isBlacklisted() terminado para: {}...", key.substring(0, Math.min(40, key.length()))))
                .doFinally(signalType -> log.info("[BLACKLIST] Flujo isBlacklisted() finalizado con senal: {} para: {}...", signalType, key.substring(0, Math.min(40, key.length()))));
    }
}
