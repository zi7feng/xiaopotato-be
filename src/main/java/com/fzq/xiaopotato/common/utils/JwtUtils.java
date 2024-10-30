package com.fzq.xiaopotato.common.utils;

import com.fzq.xiaopotato.model.vo.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    private static final String SECRET_KEY = "potatopotatopotatopotatopotatopotatopotatopotato";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 12; // 12 hour
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // generate jwt
    public String generateToken(UserVO user) {
        return Jwts.builder()
                .setSubject(user.getUserAccount())
                .claim("id", user.getId())
                .claim("role", user.getUserRole())
                .claim("random", UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    // add old token to the blacklist (set expiry to the remaining token validity)
    public void addToBlacklist(String token) {
        long expiration = getClaimsFromToken(token).getExpiration().getTime() - System.currentTimeMillis();
        if (expiration > 0) {
            String tokenKey = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(tokenKey, true, expiration, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        String tokenKey = "token:blacklist:" + token;
        boolean result = Boolean.TRUE.equals(redisTemplate.opsForValue().get(tokenKey));
        return result;
    }
}
