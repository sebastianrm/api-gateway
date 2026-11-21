# Node Description Batch 2 of 3

Graphify is running in assistant/skill mode (no API key). You are the host
assistant (Claude Code / Codex / Gemini CLI). Read the prompt below and write
your JSON answer to the answer file.

## Prompt

You are documenting nodes in a knowledge graph.
For each entry below, write ONE concise factual plain-language sentence
describing what it is or does. Use only the provided context.
For a code symbol (kind=code-symbol — a function, class, or constant),
describe what the function/symbol does based on its name, source location
and neighbors — e.g. "Resolves the configured ontology profile from graphify.yaml.".
Write every description in English (en). Do not switch languages.
No marketing language.
Respond ONLY with a JSON object mapping each node id (as a string) to its
one-sentence description — no prose, no markdown fences.

- "config_gatewaycorsproperties_gatewaycorsproperties_setallowcredentials": ".setAllowCredentials()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L134 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_setallowedheaders": ".setAllowedHeaders()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L98 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_setallowedmethods": ".setAllowedMethods()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L80 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_setallowedorigins": ".setAllowedOrigins()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L62 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_setexposedheaders": ".setExposedHeaders()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L116 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_setmaxage": ".setMaxAge()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L152 | neighbors=[GatewayCorsProperties]
- "config_gatewaypublicpathproperties": "GatewayPublicPathProperties.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayPublicPathProperties.java:L1 | neighbors=[GatewayPublicPathProperties]
- "config_gatewaypublicpathproperties_gatewaypublicpathproperties_getpublicpaths": ".getPublicPaths()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayPublicPathProperties.java:L28 | neighbors=[GatewayPublicPathProperties]
- "config_gatewaypublicpathproperties_gatewaypublicpathproperties_setpublicpaths": ".setPublicPaths()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayPublicPathProperties.java:L37 | neighbors=[GatewayPublicPathProperties]
- "config_gatewaysecurityproperties": "GatewaySecurityProperties.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L1 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_getexpiration": ".getExpiration()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L90 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_getissuer": ".getIssuer()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L72 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_getmaxlifetimegrace": ".getMaxLifetimeGrace()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L108 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_getsecret": ".getSecret()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L54 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_setexpiration": ".setExpiration()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L99 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_setissuer": ".setIssuer()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L81 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_setmaxlifetimegrace": ".setMaxLifetimeGrace()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L117 | neighbors=[GatewaySecurityProperties]
- "config_gatewaysecurityproperties_gatewaysecurityproperties_setsecret": ".setSecret()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L63 | neighbors=[GatewaySecurityProperties]
- "config_resilience4jgatewayconfigurationtest": "Resilience4jGatewayConfigurationTest.java" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/config/Resilience4jGatewayConfigurationTest.java:L1 | neighbors=[Resilience4jGatewayConfigurationTest]
- "config_resilience4jgatewayconfigurationtest_resilience4jgatewayconfigurationtest_shouldapplycircuitbreakersettingstodefaultbreaker": ".shouldApplyCircuitBreakerSettingsToDefaultBreaker()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/config/Resilience4jGatewayConfigurationTest.java:L55 | neighbors=[Resilience4jGatewayConfigurationTest]
- "config_resilience4jgatewayconfigurationtest_resilience4jgatewayconfigurationtest_shouldapplytwosecondstimeouttodefaulttimelimiter": ".shouldApplyTwoSecondsTimeoutToDefaultTimeLimiter()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/config/Resilience4jGatewayConfigurationTest.java:L46 | neighbors=[Resilience4jGatewayConfigurationTest]
- "dto_apierror": "ApiError.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/dto/ApiError.java:L1 | neighbors=[of()]
- "dto_apierror_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/dto/ApiError.java:L50 | neighbors=[ApiError.java]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_getorder": ".getOrder()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L105 | neighbors=[JwtAuthenticationGlobalFilter]
- "filter_jwtauthenticationglobalfiltertest": "JwtAuthenticationGlobalFilterTest.java" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L1 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_setup": ".setUp()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L52 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_shouldallowpublicpathwithouttoken": ".shouldAllowPublicPathWithoutToken()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L63 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_shouldpropagateidentityheaderswhentokenisvalid": ".shouldPropagateIdentityHeadersWhenTokenIsValid()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L102 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_shouldrejectinvalidtoken": ".shouldRejectInvalidToken()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L122 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_shouldrejectprotectedpathwithmalformedauthorizationheader": ".shouldRejectProtectedPathWithMalformedAuthorizationHeader()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L89 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest_shouldrejectprotectedpathwithouttoken": ".shouldRejectProtectedPathWithoutToken()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L74 | neighbors=[JwtAuthenticationGlobalFilterTest]
- "gateway_apigatewayapplication": "ApiGatewayApplication.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/ApiGatewayApplication.java:L1 | neighbors=[ApiGatewayApplication]
- "gateway_apigatewayapplication_apigatewayapplication_main": ".main()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/ApiGatewayApplication.java:L30 | neighbors=[ApiGatewayApplication]
- "model_jwtprincipal": "JwtPrincipal.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/domain/model/JwtPrincipal.java:L1 | neighbors=[rolesAsHeaderValue()]
- "model_jwtprincipal_rolesasheadervalue": "rolesAsHeaderValue()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/domain/model/JwtPrincipal.java:L35 | neighbors=[JwtPrincipal.java]
- "rest_fallbackcontroller": "FallbackController.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/FallbackController.java:L1 | neighbors=[FallbackController]
- "rest_fallbackcontroller_fallbackcontroller_fallback": ".fallback()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/FallbackController.java:L69 | neighbors=[FallbackController]
- "rest_fallbackcontroller_fallbackcontroller_health": ".health()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/FallbackController.java:L53 | neighbors=[FallbackController]
- "rest_gatewayhttpflowintegrationtest": "GatewayHttpFlowIntegrationTest.java" | kind=code-symbol | source=src/test/java/com/yanki/gateway/interfaces/rest/GatewayHttpFlowIntegrationTest.java:L1 | neighbors=[GatewayHttpFlowIntegrationTest]
- "rest_gatewayhttpflowintegrationtest_gatewayhttpflowintegrationtest_shouldanswercorspreflight": ".shouldAnswerCorsPreflight()" | kind=code-symbol | source=src/test/java/com/yanki/gateway/interfaces/rest/GatewayHttpFlowIntegrationTest.java:L98 | neighbors=[GatewayHttpFlowIntegrationTest]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\api-gateway\.graphify\description-instructions\batch-001.json

Keep each description factual and concise (one sentence). No markdown, no prose
outside the JSON object. It is acceptable to omit a node if context is
insufficient — but include every node you can ground confidently.

Example answer format:
```json
{
  "node_id_1": "Resolves the configured ontology profile from graphify.yaml.",
  "node_id_2": "Colonel James Barclay, an antagonist in The Crooked Man."
}
```
