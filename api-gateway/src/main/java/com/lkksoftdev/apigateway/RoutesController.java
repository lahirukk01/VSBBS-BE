package com.lkksoftdev.apigateway;

import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    private final RouteDefinitionLocator routeDefinitionLocator;
    private final RouteLocator routeLocator;

    public RoutesController(RouteDefinitionLocator routeDefinitionLocator, RouteLocator routeLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.routeLocator = routeLocator;
    }

    @GetMapping("/definitions")
    public Flux<String> getRouteDefinitions() {
        return routeDefinitionLocator.getRouteDefinitions()
                .map(routeDefinition -> routeDefinition.getId() + " - " + routeDefinition.getUri());
    }

    @GetMapping("/active")
    public Flux<String> getActiveRoutes() {
        return routeLocator.getRoutes()
                .map(route -> route.getId() + " - " + route.getUri());
    }
}