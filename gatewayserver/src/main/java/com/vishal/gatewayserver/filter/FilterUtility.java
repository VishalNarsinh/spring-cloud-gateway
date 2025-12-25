package com.vishal.gatewayserver.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {

	public static final String CORRELATION_ID = "correlation-id";

	public String getCorrelationId(HttpHeaders headers) {
		return headers.getFirst(CORRELATION_ID);
//		Optional.ofNullable(headers.get(CORRELATION_ID)).flatMap(list -> list.stream().findFirst()).orElse(null);
	}

	private ServerWebExchange setRequestHeader(ServerWebExchange exchange, String headerName, String headerValue) {
		return exchange.mutate().request(exchange.getRequest().mutate().header(headerName, headerValue).build()).build();
	}

	public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
		return setRequestHeader(exchange, CORRELATION_ID, correlationId);
	}

}
