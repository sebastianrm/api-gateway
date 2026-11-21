package com.yanki.gateway.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.yanki.gateway.domain.model.JwtPrincipal;
import com.yanki.gateway.infrastructure.cache.TokenBlacklistService;
import reactor.core.publisher.Mono;

/**
 * Pruebas unitarias de {@link JwtTokenValidator}.
 */
class JwtTokenValidatorTest {

    private static final String SECRET = "yanki-platform-unit-test-secret-key-2026";
    private static final String OTHER_SECRET = "yanki-platform-other-secret-key-2026-abc";
    private static final String ISSUER = "yanki-platform";

    private JwtTokenValidator validator;
    private TokenBlacklistService blacklistServiceMock;

    @BeforeEach
    void setUp() {
        blacklistServiceMock = Mockito.mock(TokenBlacklistService.class);
        Mockito.when(blacklistServiceMock.isBlacklisted(Mockito.anyString()))
                .thenReturn(Mono.just(false));
        validator = new JwtTokenValidator(properties(SECRET), blacklistServiceMock);
    }

    @Test
    @DisplayName("Acepta un token valido y extrae la identidad del usuario")
    void shouldAcceptValidToken() {
        Date issuedAt = new Date(System.currentTimeMillis() - 1_000L);
        Date expiration = new Date(System.currentTimeMillis() + 3_600_000L);
        String token = buildToken(signingKey(SECRET), ISSUER, "user-123", "juan.perez", List.of("ROLE_ADMIN", "ROLE_USER"),
                issuedAt, expiration);

        Optional<JwtPrincipal> result = validator.validate(token);

        assertThat(result).isPresent();
        JwtPrincipal principal = result.orElseThrow();
        assertThat(principal.userId()).isEqualTo("user-123");
        assertThat(principal.username()).isEqualTo("juan.perez");
        assertThat(principal.roles()).containsExactly("ROLE_ADMIN", "ROLE_USER");
        assertThat(principal.rolesAsHeaderValue()).isEqualTo("ROLE_ADMIN,ROLE_USER");
    }

    @Test
    @DisplayName("Rechaza un token expirado")
    void shouldRejectExpiredToken() {
        Date issuedAt = new Date(System.currentTimeMillis() - 7_200_000L);
        Date expiration = new Date(System.currentTimeMillis() - 3_600_000L);
        String token = buildToken(signingKey(SECRET), ISSUER, "user-123", "juan.perez", List.of("ROLE_USER"), issuedAt,
                expiration);

        assertThat(validator.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("Rechaza un token firmado con otro secreto")
    void shouldRejectTokenWithInvalidSignature() {
        Date issuedAt = new Date(System.currentTimeMillis() - 1_000L);
        Date expiration = new Date(System.currentTimeMillis() + 3_600_000L);
        String token = buildToken(signingKey(OTHER_SECRET), ISSUER, "user-123", "juan.perez", List.of("ROLE_USER"),
                issuedAt, expiration);

        assertThat(validator.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("Rechaza un token malformado")
    void shouldRejectMalformedToken() {
        assertThat(validator.validate("esto-no-es-un-jwt")).isEmpty();
        assertThat(validator.validate("")).isEmpty();
        assertThat(validator.validate(null)).isEmpty();
    }

    @Test
    @DisplayName("Rechaza un token emitido por otro issuer")
    void shouldRejectTokenWithUnexpectedIssuer() {
        Date issuedAt = new Date(System.currentTimeMillis() - 1_000L);
        Date expiration = new Date(System.currentTimeMillis() + 3_600_000L);
        String token = buildToken(signingKey(SECRET), "otro-issuer", "user-123", "juan.perez", List.of("ROLE_USER"),
                issuedAt, expiration);

        assertThat(validator.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("Rechaza un token sin claim de expiracion")
    void shouldRejectTokenWithoutExpiration() {
        String token = Jwts.builder()
                .subject("user-123")
                .issuer(ISSUER)
                .claim("username", "juan.perez")
                .issuedAt(new Date(System.currentTimeMillis() - 1_000L))
                .signWith(signingKey(SECRET), Jwts.SIG.HS256)
                .compact();

        assertThat(validator.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("Rechaza un token con vida maxima superior a la configurada")
    void shouldRejectTokenExceedingMaximumLifetime() {
        Date issuedAt = new Date(System.currentTimeMillis() - 1_000L);
        Date expiration = new Date(System.currentTimeMillis() + Duration.ofDays(30).toMillis());
        String token = buildToken(signingKey(SECRET), ISSUER, "user-123", "juan.perez", List.of("ROLE_USER"), issuedAt,
                expiration);

        assertThat(validator.validate(token)).isEmpty();
    }

    @Test
    @DisplayName("Falla al arrancar si el secreto es mas corto que 32 bytes")
    void shouldFailWhenSecretIsTooShort() {
        TokenBlacklistService mock = Mockito.mock(TokenBlacklistService.class);
        assertThatThrownBy(() -> new JwtTokenValidator(properties("corto"), mock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32");
    }

    private static GatewaySecurityProperties properties(String secret) {
        GatewaySecurityProperties properties = new GatewaySecurityProperties();
        properties.setSecret(secret);
        properties.setIssuer(ISSUER);
        properties.setExpiration(Duration.ofHours(1));
        properties.setMaxLifetimeGrace(Duration.ofMinutes(5));
        return properties;
    }

    private static SecretKey signingKey(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), JwtTokenValidator.HMAC_SHA256);
    }

    private static String buildToken(SecretKey key, String issuer, String subject, String username, Object roles,
            Date issuedAt, Date expiration) {
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claim("username", username)
                .claim("roles", roles)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
