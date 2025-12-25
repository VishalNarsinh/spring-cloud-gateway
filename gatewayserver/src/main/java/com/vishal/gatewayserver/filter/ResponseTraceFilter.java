package com.vishal.gatewayserver.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class ResponseTraceFilter {

	@Bean
	public GlobalFilter postGlobalFilter(FilterUtility filterUtility){
		return (exchange,chain)->{
			return chain.filter(exchange).then(Mono.fromRunnable(()->{
				HttpHeaders headers = exchange.getRequest().getHeaders();
				String correlationId = filterUtility.getCorrelationId(headers);
				log.debug("Updated the {} to the outbound headers: {}",FilterUtility.CORRELATION_ID,correlationId);
				exchange.getResponse().getHeaders().add(FilterUtility.CORRELATION_ID,correlationId);
			}));
		};
	}
}
