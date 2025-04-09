package dev.bryanlam.stockwatch.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfDoubleSubmitFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) ||
            "PUT".equalsIgnoreCase(request.getMethod()) ||
            "DELETE".equalsIgnoreCase(request.getMethod())) {

            String csrfHeader = request.getHeader("X-CSRF-TOKEN");
            String csrfCookie = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("XSRF-TOKEN".equals(cookie.getName())) {
                        csrfCookie = cookie.getValue();
                        break;
                    }
                }
            }

            if (csrfHeader == null || csrfCookie == null || !csrfHeader.equals(csrfCookie)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

