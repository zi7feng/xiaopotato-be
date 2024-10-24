package com.fzq.xiaopotato.common.utils;

import com.fzq.xiaopotato.model.vo.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final String SECRET_KEY = "potatopotatopotatopotatopotatopotatopotatopotato";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 3;

    // generate jwt
    public String generateToken(UserVO user) {
        return Jwts.builder()
                .setSubject(user.getUserAccount())
                .claim("id", user.getId())
                .claim("role", user.getUserRole())
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

}
