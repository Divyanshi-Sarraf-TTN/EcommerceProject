//package com.example.EcommerceProject.EcommerceProject.JWT;
//
//import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Component
//public class JwtService {
//
//    // Secret Key for signing the JWT. It should be kept private.
//    private static final String SECRET = "TmV3U2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzMTIzNDU2Nzg=";
//
//    // Generates a JWT token for the given userName.
//    public String generateToken(User user, String userName) {
//        // Prepare claims for the token
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role",user.getRole());
//
//        // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userName)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 3 minutes
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();    }
//
//    // Helper method to create a JWT token
//    public String refreshToken(User user, String userName) {
//        // Prepare claims for the token
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role",user.getRole());
//
//        // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userName)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24*60)) // 3 minutes
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();    }
//
//    // Creates a signing key from the base64 encoded secret.
//    private Key getSignKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    // Extracts the userName from the JWT token.
//    public String extractUserName(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extracts the expiration date from the JWT token.
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Extracts a specific claim from the JWT token.
//    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimResolver.apply(claims);
//    }
//
//    // Extracts all claims from the JWT token.
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // Checks if the JWT token is expired.
//    public Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Validates the JWT token against the UserDetails.
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//}
