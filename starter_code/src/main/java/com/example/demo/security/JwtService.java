package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY = "testsecret";  // Use a secure secret key!


    // Generate a JWT token
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY); // HMAC256 for signing
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())           // Add issue time
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))  // Set expiration (1 hour)
                .sign(algorithm);
    }

    // Validate and decode the JWT token
    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .build()
                .verify(token); // This will throw an exception if the token is invalid
    }
}
