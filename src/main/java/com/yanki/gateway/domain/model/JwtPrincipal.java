package com.yanki.gateway.domain.model;

import java.util.List;

/**
 * Identidad autenticada del usuario, extraida de un token JWT valido.
 *
 * <p>Es la informacion que la API Gateway propaga a los microservicios destino
 * mediante las cabeceras {@code X-User-Id}, {@code X-Username} y {@code X-Roles}.</p>
 *
 * @param userId identificador del usuario autenticado (claim {@code sub})
 * @param username nombre de usuario autenticado (claim {@code username})
 * @param roles roles del usuario, en el orden en que fueron emitidos
 */
public record JwtPrincipal(String userId, String username, List<String> roles) {

    /**
     * Normaliza la identidad para garantizar que nunca haya valores nulos.
     *
     * @param userId identificador del usuario autenticado
     * @param username nombre de usuario autenticado
     * @param roles roles del usuario
     */
    public JwtPrincipal {
        userId = userId == null ? "" : userId;
        username = username == null ? "" : username;
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    /**
     * Devuelve los roles listos para propagar en la cabecera {@code X-Roles}.
     *
     * @return roles separados por coma
     */
    public String rolesAsHeaderValue() {
        return String.join(",", roles);
    }
}
