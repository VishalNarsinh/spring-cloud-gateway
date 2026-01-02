package com.vishal.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static final String X_RESPONSE_TIME = "X-Response-Time";
	public static final String SEGMENT = "/${segment}";

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/custom/accounts/**")
						.filters(f -> f
								.rewritePath("/custom/accounts/(?<segment>.*)", SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString())
						)
						.uri("lb://ACCOUNTS"))

				.route(p -> p
						.path("/custom/cards/**")
						.filters(f -> f
								.rewritePath("/custom/cards/(?<segment>.*)", SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString())
						)
						.uri("lb://CARDS"))

				.route(p -> p
						.path("/custom/loans/**")
						.filters(f -> f
								.rewritePath("/custom/loans/(?<segment>.*)", SEGMENT)
								.addResponseHeader(X_RESPONSE_TIME, LocalDateTime.now().toString())
						)
						.uri("lb://LOANS"))
				.build();
	}
}
