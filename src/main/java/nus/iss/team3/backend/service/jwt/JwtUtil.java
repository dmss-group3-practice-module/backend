package nus.iss.team3.backend.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import nus.iss.team3.backend.entity.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
  private String SECRET_KEY =
      Base64.getEncoder()
          .encodeToString(
              "Th1sIsN0tThePassw0rdD0ntA$kMeThePasswordIAls0DontknowtheactualPassword".getBytes());

  // 4261656C64756E67
  // This1sNotThePassW0rd

  //  feats-backend-authenticate  | io.jsonwebtoken.security.WeakKeyException: The signing key's
  // size is 120 bits which is not secure enough for the HS256 algorithm.  The JWT JWA Specification
  // (RFC 7518, Section 3.2) states that keys used with HS256 MUST have a size >= 256 bits (the key
  // size must be greater than or equal to the hash output size).  Consider using the
  // io.jsonwebtoken.security.Keys class's 'secretKeyFor(SignatureAlgorithm.HS256)' method to create
  // a key guaranteed to be secure enough for HS256.  See
  // https://tools.ietf.org/html/rfc7518#section-3.2 for more information.

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserAccount userAccount) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userAccount.getName());
  }

  private String createToken(Map<String, Object> claims, String subject) {
    String output =
        Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(getSigningKey())
            .compact();
    return output;
  }

  public Boolean validateToken(String token, UserAccount userAccount) {
    final String username = extractUsername(token);
    return (username.equals(userAccount.getName()) && !isTokenExpired(token));
  }
}
