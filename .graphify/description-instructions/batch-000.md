# Node Description Batch 1 of 3

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

- "config_gatewaycorsproperties_gatewaycorsproperties": "GatewayCorsProperties" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L15 | neighbors=[GatewayCorsProperties.java, .getAllowedHeaders(), .getAllowedMethods(), .getAllowedOrigins(), .getExposedHeaders(), .getMaxAge()]
- "config_gatewaysecurityproperties_gatewaysecurityproperties": "GatewaySecurityProperties" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewaySecurityProperties.java:L18 | neighbors=[GatewaySecurityProperties.java, .getExpiration(), .getIssuer(), .getMaxLifetimeGrace(), .getSecret(), .setExpiration()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter": "JwtAuthenticationGlobalFilter" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L43 | neighbors=[JwtAuthenticationGlobalFilter.java, .filter(), .getOrder(), .isBearer(), .isPublicPath(), .unauthorized()]
- "filter_gatewayerrorhandler_gatewayerrorhandler": "GatewayErrorHandler" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L36 | neighbors=[GatewayErrorHandler.java, ErrorWebExceptionHandler, .handle(), .resolveCode(), .resolveDetails(), .resolveMessage()]
- "rest_gatewayhttpflowintegrationtest_gatewayhttpflowintegrationtest": "GatewayHttpFlowIntegrationTest" | kind=code-symbol | source=src/test/java/com/yanki/gateway/interfaces/rest/GatewayHttpFlowIntegrationTest.java:L20 | neighbors=[GatewayHttpFlowIntegrationTest.java, .shouldAnswerCorsPreflight(), .shouldExposeActuatorWithoutToken(), .shouldExposeFallbackHealthEndpoint(), .shouldExposeOpenApiWithoutToken(), .shouldRejectProtectedRouteWithInvalidT…]
- "filter_jwtauthenticationglobalfiltertest_jwtauthenticationglobalfiltertest": "JwtAuthenticationGlobalFilterTest" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilterTest.java:L41 | neighbors=[JwtAuthenticationGlobalFilterTest.java, .setUp(), .shouldAllowPublicPathWithoutToken(), .shouldPropagateIdentityHeadersWhenToke…, .shouldRejectInvalidToken(), .shouldRejectProtectedPathWithMalformed…]
- "filter_gatewayerrorhandler_gatewayerrorhandler_handle": ".handle()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L59 | neighbors=[GatewayErrorHandler, .resolveCode(), .resolveDetails(), .resolveMessage(), .resolveStatus()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_filter": ".filter()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L80 | neighbors=[JwtAuthenticationGlobalFilter, .isBearer(), .isPublicPath(), .unauthorized(), .withPrincipal()]
- "support_errorresponsewriter_errorresponsewriter": "ErrorResponseWriter" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/support/ErrorResponseWriter.java:L24 | neighbors=[ErrorResponseWriter.java, .ErrorResponseWriter(), .serialize(), .write()]
- "config_gatewaycorsconfiguration_gatewaycorsconfiguration": "GatewayCorsConfiguration" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsConfiguration.java:L16 | neighbors=[GatewayCorsConfiguration.java, .corsWebFilter(), .GatewayCorsConfiguration()]
- "config_gatewaypublicpathproperties_gatewaypublicpathproperties": "GatewayPublicPathProperties" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayPublicPathProperties.java:L15 | neighbors=[GatewayPublicPathProperties.java, .getPublicPaths(), .setPublicPaths()]
- "config_resilience4jgatewayconfigurationtest_resilience4jgatewayconfigurationtest": "Resilience4jGatewayConfigurationTest" | kind=code-symbol | source=src/test/java/com/yanki/gateway/infrastructure/config/Resilience4jGatewayConfigurationTest.java:L31 | neighbors=[Resilience4jGatewayConfigurationTest.ja…, .shouldApplyCircuitBreakerSettingsToDef…, .shouldApplyTwoSecondsTimeoutToDefaultT…]
- "filter_gatewayerrorhandler_gatewayerrorhandler_resolvedetails": ".resolveDetails()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L133 | neighbors=[GatewayErrorHandler, .handle(), .rootCause()]
- "filter_jwtauthenticationglobalfilter": "JwtAuthenticationGlobalFilter.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L1 | neighbors=[JwtAuthenticationGlobalFilter, GlobalFilter, Ordered]
- "rest_fallbackcontroller_fallbackcontroller": "FallbackController" | kind=code-symbol | source=src/main/java/com/yanki/gateway/interfaces/rest/FallbackController.java:L32 | neighbors=[FallbackController.java, .fallback(), .health()]
- "errorwebexceptionhandler": "ErrorWebExceptionHandler" | kind=code-symbol | neighbors=[GatewayErrorHandler.java, GatewayErrorHandler]
- "filter_gatewayerrorhandler": "GatewayErrorHandler.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L1 | neighbors=[ErrorWebExceptionHandler, GatewayErrorHandler]
- "filter_gatewayerrorhandler_gatewayerrorhandler_resolvecode": ".resolveCode()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L100 | neighbors=[GatewayErrorHandler, .handle()]
- "filter_gatewayerrorhandler_gatewayerrorhandler_resolvemessage": ".resolveMessage()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L119 | neighbors=[GatewayErrorHandler, .handle()]
- "filter_gatewayerrorhandler_gatewayerrorhandler_resolvestatus": ".resolveStatus()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L78 | neighbors=[GatewayErrorHandler, .handle()]
- "filter_gatewayerrorhandler_gatewayerrorhandler_rootcause": ".rootCause()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/GatewayErrorHandler.java:L147 | neighbors=[GatewayErrorHandler, .resolveDetails()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_isbearer": ".isBearer()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L118 | neighbors=[JwtAuthenticationGlobalFilter, .filter()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_ispublicpath": ".isPublicPath()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L128 | neighbors=[JwtAuthenticationGlobalFilter, .filter()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_unauthorized": ".unauthorized()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L166 | neighbors=[JwtAuthenticationGlobalFilter, .filter()]
- "filter_jwtauthenticationglobalfilter_jwtauthenticationglobalfilter_withprincipal": ".withPrincipal()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/filter/JwtAuthenticationGlobalFilter.java:L144 | neighbors=[JwtAuthenticationGlobalFilter, .filter()]
- "gateway_apigatewayapplication_apigatewayapplication": "ApiGatewayApplication" | kind=code-symbol | source=src/main/java/com/yanki/gateway/ApiGatewayApplication.java:L21 | neighbors=[ApiGatewayApplication.java, .main()]
- "globalfilter": "GlobalFilter" | kind=code-symbol | neighbors=[JwtAuthenticationGlobalFilter.java, JwtAuthenticationGlobalFilter]
- "ordered": "Ordered" | kind=code-symbol | neighbors=[JwtAuthenticationGlobalFilter.java, JwtAuthenticationGlobalFilter]
- "support_errorresponsewriter_errorresponsewriter_serialize": ".serialize()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/support/ErrorResponseWriter.java:L63 | neighbors=[ErrorResponseWriter, .write()]
- "support_errorresponsewriter_errorresponsewriter_write": ".write()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/support/ErrorResponseWriter.java:L48 | neighbors=[ErrorResponseWriter, .serialize()]
- "config_gatewaycorsconfiguration": "GatewayCorsConfiguration.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsConfiguration.java:L1 | neighbors=[GatewayCorsConfiguration]
- "config_gatewaycorsconfiguration_gatewaycorsconfiguration_corswebfilter": ".corsWebFilter()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsConfiguration.java:L35 | neighbors=[GatewayCorsConfiguration]
- "config_gatewaycorsconfiguration_gatewaycorsconfiguration_gatewaycorsconfiguration": ".GatewayCorsConfiguration()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsConfiguration.java:L26 | neighbors=[GatewayCorsConfiguration]
- "config_gatewaycorsproperties": "GatewayCorsProperties.java" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L1 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_getallowedheaders": ".getAllowedHeaders()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L89 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_getallowedmethods": ".getAllowedMethods()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L71 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_getallowedorigins": ".getAllowedOrigins()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L53 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_getexposedheaders": ".getExposedHeaders()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L107 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_getmaxage": ".getMaxAge()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L143 | neighbors=[GatewayCorsProperties]
- "config_gatewaycorsproperties_gatewaycorsproperties_isallowcredentials": ".isAllowCredentials()" | kind=code-symbol | source=src/main/java/com/yanki/gateway/infrastructure/config/GatewayCorsProperties.java:L125 | neighbors=[GatewayCorsProperties]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\api-gateway\.graphify\description-instructions\batch-000.json

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
