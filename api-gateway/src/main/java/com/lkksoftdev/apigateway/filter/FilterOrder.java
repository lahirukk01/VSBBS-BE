package com.lkksoftdev.apigateway.filter;

import org.springframework.core.Ordered;

public class FilterOrder {
    public static final int LOGGING_GLOBAL_FILTER = Ordered.HIGHEST_PRECEDENCE;
    public static final int ROUTE_AUTH_FILTER = Ordered.HIGHEST_PRECEDENCE + 1;
}
