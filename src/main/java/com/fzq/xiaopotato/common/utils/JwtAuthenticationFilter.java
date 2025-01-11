package com.fzq.xiaopotato.common.utils;

import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    // Paths that do not require JWT authentication
    private static final List<String> EXCLUDED_PATHS = List.of("/user/current","/user/register", "/user/login", "/v3/api-docs/**",         // swagger
            "/webjars/**",            // swagger-ui webjars
            "/swagger-resources/**",  // swagger-ui resources
            "/configuration/**",      // swagger configuration
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/*.css",
            "/v3/api-docs",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui.css",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/*.png",
            "/swagger-ui.html",
            "/swagger-ui/index.html"
            ,
            "/upload.html",
            "/common/upload"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();  // Get the request path

        // If the request path is excluded from authentication, allow the request to pass
        if (EXCLUDED_PATHS.contains(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // get JWT from the Authorization header

        if (request.getHeader("Authorization") == null) {
//            throw new BusinessException(ErrorCode.NULL_ERROR);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header is missing");
            return;
        }
        String token = java.net.URLDecoder.decode(request.getHeader("Authorization"), "UTF-8");

        // validate JWT
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // remove Bearer prefix
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token); // check the jwt is valid

                // check if the token is expired
                if (jwtUtils.isTokenExpired(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                if (jwtUtils.isTokenBlacklisted(token)) {
                    // if in the blacklist, return 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
