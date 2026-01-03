package com.task.tracker.authentication.service;

import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtService {


    public static final String SECRET = "4367286B59703373317639792F423F4528482B4D6251695468576D5A71347437";

    @Autowired
    private UserRepository userRepository;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true; // token is valid
        } catch (Exception e) {
            return false; // token invalid / expired / tampered
        }
    }



    public String generateToken(String userName) throws Exception {
        try{
            Map<String, Object> claims = new HashMap<>();
            Optional<User> user = userRepository.findByUserName(userName);
            claims.put("roles", user.get().getRoles());
            return createToken(claims, userName);
        }
        catch (Exception e){
            throw new Exception("User not found");
        }

    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractAllClaims(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token, Claims::getSubject);
    }
}

