package ru.rvip.gateway.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class InternalSearchBlockFilter implements WebFilter {

    static final String BLOCKED_PATH = "/api/contracts/search";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        if (isBlocked(path)) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
        return chain.filter(exchange);
    }

    static boolean isBlocked(String path) {
        if (path == null) {
            return false;
        }
        String normalized = path.endsWith("/") && path.length() > 1
                ? path.substring(0, path.length() - 1)
                : path;
        return BLOCKED_PATH.equals(normalized);
    }
}
