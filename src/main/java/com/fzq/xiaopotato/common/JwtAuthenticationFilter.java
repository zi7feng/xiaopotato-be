package com.fzq.xiaopotato.common;

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

    // 定义不需要 JWT 认证的路径
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
            "/post/upload"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();  // 获取请求路径

        // 如果请求路径在不需要认证的路径中，直接放行
        if (EXCLUDED_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取请求头中的 JWT
        String token = request.getHeader("Authorization");

        // 验证 JWT
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 Bearer 前缀
            try {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                // 这里可以将用户信息设置到上下文中，例如 SecurityContextHolder
            } catch (Exception e) {
                // JWT无效或过期，返回 401 状态码
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // 如果没有提供 JWT 或者格式错误，返回 401 状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 放行请求
        filterChain.doFilter(request, response);
    }
}
