package com.lkksoftdev.loanservice.filter;

import com.lkksoftdev.loanservice.customAnnotation.BearerToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class BearerTokenExtractor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        Method method = ((HandlerMethod) handler).getMethod();

        if (!method.isAnnotationPresent(BearerToken.class)) {
            return true;
        }


        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String bearerToken = authorizationHeader.substring(7);
            request.setAttribute("bearerToken", bearerToken);
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        request.removeAttribute("bearerToken");
    }
}
