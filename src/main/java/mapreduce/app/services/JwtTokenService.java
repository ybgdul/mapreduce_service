package mapreduce.app.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenService {
    
    @Value("${secret.key}")
    private String secret;

    private long validityInMilliseconds = 3600000;

    private Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private SecretKey signingKey;

    @PostConstruct
    protected void init() {
        try {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String generateToken(UserDetails user) {
        List<String> roleNames = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder().subject(user.getUsername()).claim("roles", roleNames).issuedAt(now).expiration(expiration).signWith(signingKey).compact();
    }

    public String extractUsername(String token) { 
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) { 
        Claims claims = extractClaims(token);

        return claims.getSubject().equals(user.getUsername()) && claims.getExpiration().after(new Date());
    }

    public Claims extractClaims(String token) { 
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
    }

    public String getUsername(String token) { 
        Claims claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }


    
}
