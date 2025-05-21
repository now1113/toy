package site.kimnow.toy.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;
    private static final String ROLE = "role";

    public String createAccessToken(String userId, String authority) {
        return createToken(userId, authority, jwtProperties.getAccessTokenExpirationMills());
    }

    public String createRefreshToken(String userId, String authority) {
        return createToken(userId, authority, jwtProperties.getRefreshTokenExpirationMills());
    }

    // 토큰 생성
    public String createToken(String userId, String authority, long expireMills) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMills);

        return Jwts.builder()
                .setSubject(userId)
                .claim(ROLE, authority)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        Claims claims = getClaims(token);
        return claims.get(ROLE, String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
