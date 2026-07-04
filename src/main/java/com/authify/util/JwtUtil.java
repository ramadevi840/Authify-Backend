//package com.authify.util;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//
//@Component
//public class JwtUtil {
//
//	private static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000;
//	
//	@Value("${jwt.secret.key}")
//	private String SECRET_KEY;
//	 public String generateToken(UserDetails userDetails) {
//	        Map<String, Object> claims = new HashMap<>();
//	        return createToken(claims, userDetails.getUsername());
//	    }
//	 private String createToken(Map<String, Object> claims, String username) {
//	        return Jwts.builder()
//	                .setClaims(claims)
//	                .setSubject(username)
//	                .setIssuedAt(new Date(System.currentTimeMillis()))
//	                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
//	                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//	                .compact();
//	    }
//	 
//	 
//	 private Claims extractAllClaims(String token) {
//		 return Jwts.parser()
//				 .setSigningKey(SECRET_KEY)
//				 .parseClaimsJws(token)
//				 .getBody();
//	 }
//	 
//	 public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//	        final Claims claims = extractAllClaims(token);
//	        return claimsResolver.apply(claims);
//	    }
//	 
//
//// ✅ Extract username
//public String extractEmail(String token) {
//    return extractClaim(token, Claims::getSubject);
//}
//
//// ✅ Extract expiration
//public Date extractExpiration(String token) {
//    return extractClaim(token, Claims::getExpiration);
//}
//
//// ✅ Check if expired
//private boolean isTokenExpired(String token) {
//    return extractExpiration(token).before(new Date());
//}
//
//// ✅ Validate token
//public boolean validateToken(String token, UserDetails userDetails) {
//    final String email= extractEmail(token);
//    return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
//}
//	 
//	 
//}





package com.authify.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000;

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)
                )
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        final String email = extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}
