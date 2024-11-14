package nus.iss.team3.backend.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import nus.iss.team3.backend.entity.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

  @Value(
      "${Just.Wonder.This:Th1sIsN0tThePassw0rdD0ntA$kMeThePasswordIAls0DontknowtheactualPassword}")
  private String SECRET_DOOR_OPENER;

  private String SECRET_KEY = null;

  @PostConstruct
  public void postConstruct() {
    SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_DOOR_OPENER.getBytes());
  }

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
