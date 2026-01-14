import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerateToken {
    private static final String SECRET = "fruit-sale-secret-key-must-be-at-least-256-bits-long-for-hs256";
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String createToken(Long userId, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", type);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    public static void main(String[] args) {
        // 生成代理用户 ID 14 的 token
        String token = createToken(14L, "user");
        System.out.println(token);
    }
}
