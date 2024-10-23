package com.fzq.xiaopotato.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Documentation", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")  // 定义安全要求，指定 Bearer 认证
)
@SecurityScheme(
        name = "bearerAuth",  // 与 SecurityRequirement 中的 name 一致
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"  // 使用 JWT 作为 Token 格式
)
public class SwaggerConfig {
}
