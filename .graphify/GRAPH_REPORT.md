# Graph Report - .  (2026-11-09)

## Corpus Check
- Corpus is ~4,701 words - fits in a single context window. You may not need a graph.

## Summary
- 98 nodes · 96 edges · 13 communities detected
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output
- Edge kinds: method: 65 · contains: 15 · calls: 10 · implements: 3 · imports: 3


## Input Scope
- Requested: auto
- Resolved: all (source: default-auto)
- Included files: 15 · Candidates: recursive
- Excluded: 0 untracked · 0 ignored · 2 sensitive · 0 missing committed
## God Nodes (most connected - your core abstractions)
1. `GatewayCorsProperties` - 13 edges
2. `GatewaySecurityProperties` - 9 edges
3. `JwtAuthenticationGlobalFilter` - 9 edges
4. `GatewayErrorHandlerTest` - 9 edges
5. `GatewayErrorHandler` - 8 edges
6. `GatewayHttpFlowIntegrationTest` - 8 edges
7. `JwtAuthenticationGlobalFilterTest` - 7 edges
8. `ErrorResponseWriter` - 4 edges
9. `GatewayCorsConfiguration` - 3 edges
10. `GatewayPublicPathProperties` - 3 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.14
Nodes (1): GatewayCorsProperties

### Community 1 - "Community 1"
Cohesion: 0.20
Nodes (1): GatewaySecurityProperties

### Community 2 - "Community 2"
Cohesion: 0.20
Nodes (1): GatewayErrorHandlerTest

### Community 3 - "Community 3"
Cohesion: 0.33
Nodes (3): JwtAuthenticationGlobalFilter, GlobalFilter, Ordered

### Community 4 - "Community 4"
Cohesion: 0.39
Nodes (2): ErrorWebExceptionHandler, GatewayErrorHandler

### Community 5 - "Community 5"
Cohesion: 0.22
Nodes (1): GatewayHttpFlowIntegrationTest

### Community 6 - "Community 6"
Cohesion: 0.25
Nodes (1): JwtAuthenticationGlobalFilterTest

### Community 7 - "Community 7"
Cohesion: 0.50
Nodes (1): ErrorResponseWriter

### Community 8 - "Community 8"
Cohesion: 0.50
Nodes (1): GatewayCorsConfiguration

### Community 9 - "Community 9"
Cohesion: 0.50
Nodes (1): GatewayPublicPathProperties

### Community 10 - "Community 10"
Cohesion: 0.50
Nodes (1): Resilience4jGatewayConfigurationTest

### Community 11 - "Community 11"
Cohesion: 0.50
Nodes (1): FallbackController

### Community 12 - "Community 12"
Cohesion: 0.67
Nodes (1): ApiGatewayApplication

## Knowledge Gaps
- **Thin community `Community 0`** (1 nodes): `GatewayCorsProperties`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 1`** (1 nodes): `GatewaySecurityProperties`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 2`** (1 nodes): `GatewayErrorHandlerTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 4`** (2 nodes): `ErrorWebExceptionHandler`, `GatewayErrorHandler`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 5`** (1 nodes): `GatewayHttpFlowIntegrationTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 6`** (1 nodes): `JwtAuthenticationGlobalFilterTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 7`** (1 nodes): `ErrorResponseWriter`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 8`** (1 nodes): `GatewayCorsConfiguration`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (1 nodes): `GatewayPublicPathProperties`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (1 nodes): `Resilience4jGatewayConfigurationTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (1 nodes): `FallbackController`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (1 nodes): `ApiGatewayApplication`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._