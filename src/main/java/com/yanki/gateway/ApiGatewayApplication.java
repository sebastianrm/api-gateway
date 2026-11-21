package com.yanki.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point of the api-gateway microservice.
 *
 * <p>Part of the Yanki banking platform. The service follows the
 * database-per-service pattern: it owns and accesses only its own MongoDB
 * database and never reads nor writes the data owned by another
 * microservice. Cross-service collaboration is performed through Kafka
 * events and never through direct REST calls.</p>
 *
 * <p>The API Gateway is the single entry point of the platform. It routes the
 * REST traffic to every microservice (through Eureka service discovery),
 * validates the JWT bearer token of the caller and protects every downstream
 * call with a Resilience4j circuit breaker.</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiGatewayApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments forwarded to the Spring context
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}