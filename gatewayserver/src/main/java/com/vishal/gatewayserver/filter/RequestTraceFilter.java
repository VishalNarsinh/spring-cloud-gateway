package com.vishal.gatewayserver.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RequestTraceFilter implements GlobalFilter {

	private final FilterUtility filterUtility;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		if(isCorrelationIdPresent(headers)) {
			log.debug("{} found in RequestTraceFilter: {}",FilterUtility.CORRELATION_ID,filterUtility.getCorrelationId(headers));
		}else {
			String correlationId = generateCorrelationId();
			exchange = filterUtility.setCorrelationId(exchange,correlationId);
			log.debug("{} generated in RequestTraceFilter: {}",FilterUtility.CORRELATION_ID,correlationId);
		}
		return chain.filter(exchange);
	}

	private boolean isCorrelationIdPresent(HttpHeaders headers){
		return Objects.nonNull(filterUtility.getCorrelationId(headers));
	}

	private String generateCorrelationId(){
		return java.util.UUID.randomUUID().toString();
	}
}
