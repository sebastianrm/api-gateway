# Community Labeling

Graphify is running in assistant/skill mode (no API key). You are the host
assistant (Claude Code / Codex / Gemini CLI). Read the community listing below
and write 2-5 word plain-language names for each.

## Language

Write every name in English (en). Do not switch languages.

## Communities

Community 0: GatewayCorsProperties, GatewayCorsProperties.java, .getAllowedHeaders(, .getAllowedMethods(, .getAllowedOrigins(, .getExposedHeaders(, .getMaxAge(, .isAllowCredentials(, .setAllowCredentials(, .setAllowedHeaders(, .setAllowedMethods(, .setAllowedOrigins(
Community 1: GatewaySecurityProperties, GatewaySecurityProperties.java, .getExpiration(, .getIssuer(, .getMaxLifetimeGrace(, .getSecret(, .setExpiration(, .setIssuer(, .setMaxLifetimeGrace(, .setSecret(
Community 2: JwtAuthenticationGlobalFilter, JwtAuthenticationGlobalFilter.java, .filter(, .getOrder(, .isBearer(, .isPublicPath(, .unauthorized(, .withPrincipal(, GlobalFilter, Ordered
Community 3: GatewayErrorHandler, ErrorWebExceptionHandler, GatewayErrorHandler.java, .handle(, .resolveCode(, .resolveDetails(, .resolveMessage(, .resolveStatus(, .rootCause(
Community 4: GatewayHttpFlowIntegrationTest, GatewayHttpFlowIntegrationTest.java, .shouldAnswerCorsPreflight(, .shouldExposeActuatorWithoutToken(, .shouldExposeFallbackHealthEndpoint(, .shouldExposeOpenApiWithoutToken(, .shouldRejectProtectedRouteWithInvalidToken(, .shouldRejectProtectedRouteWithoutToken(, .shouldReturnServiceUnavailableWhenDestinationIsDown(
Community 5: JwtAuthenticationGlobalFilterTest, JwtAuthenticationGlobalFilterTest.java, .setUp(, .shouldAllowPublicPathWithoutToken(, .shouldPropagateIdentityHeadersWhenTokenIsValid(, .shouldRejectInvalidToken(, .shouldRejectProtectedPathWithMalformedAuthorizationHeader(, .shouldRejectProtectedPathWithoutToken(
Community 6: ErrorResponseWriter, ErrorResponseWriter.java, .ErrorResponseWriter(, .serialize(, .write(
Community 7: GatewayCorsConfiguration, GatewayCorsConfiguration.java, .corsWebFilter(, .GatewayCorsConfiguration(
Community 8: GatewayPublicPathProperties, GatewayPublicPathProperties.java, .getPublicPaths(, .setPublicPaths(
Community 9: Resilience4jGatewayConfigurationTest.java, Resilience4jGatewayConfigurationTest, .shouldApplyCircuitBreakerSettingsToDefaultBreaker(, .shouldApplyTwoSecondsTimeoutToDefaultTimeLimiter(
Community 10: FallbackController, FallbackController.java, .fallback(, .health(
Community 11: ApiGatewayApplication.java, ApiGatewayApplication, .main(
Community 12: ApiError.java, of(
Community 13: JwtPrincipal.java, rolesAsHeaderValue(

## Instructions

Write a single JSON object mapping each community id (as a string) to its
2-5 word name to: c:\Users\semorale\proyectos\yanki-accounts-service\api-gateway\.graphify\label-instructions\communities.json

Example:
```json
{
  "0": "Authentication Flow",
  "1": "Authentication Flow",
  "2": "Authentication Flow"
}
```

Then re-run `graphify update` (or `graphify label`) to ingest the names.
