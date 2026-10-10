package ru.rvip.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AccessLogGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long started = System.nanoTime();
        AtomicBoolean logged = new AtomicBoolean(false);
        Runnable writeLog = () -> {
            if (!logged.compareAndSet(false, true)) {
                return;
            }
            ServerHttpRequest request = exchange.getRequest();
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            String target = route == null ? "gateway" : route.getId();
            int status = exchange.getResponse().getStatusCode() == null
                    ? 0
                    : exchange.getResponse().getStatusCode().value();
            long millis = (System.nanoTime() - started) / 1_000_000;
            String query = request.getURI().getRawQuery();
            String path = query == null ? request.getURI().getRawPath() : request.getURI().getRawPath() + "?" + query;
            log.info("{} {} -> {} status={} {}ms", request.getMethod(), path, target, status, millis);
        };
        exchange.getResponse().beforeCommit(() -> {
            writeLog.run();
            return Mono.empty();
        });
        return chain.filter(exchange).doFinally(signal -> writeLog.run());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
