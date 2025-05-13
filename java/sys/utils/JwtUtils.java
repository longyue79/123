package sys.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    private static final Key SIGN_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRE = 1000 * 60 * 60 * 24; // 24小时有效

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SIGN_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public static Claims getClaimsByToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SIGN_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }
}
