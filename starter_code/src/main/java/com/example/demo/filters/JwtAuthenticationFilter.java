package com.example.demo.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final  JwtService jwtService;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService1) {
        super(authenticationManager);
        this.jwtService = jwtService1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        if (token != null) {
            try {
                // Validate the JWT token
                DecodedJWT decodedJWT = jwtService.validateToken(token);

                // Extract the username from the JWT
                String username = decodedJWT.getSubject();

                // Set the authentication context (this can be customized to load user details)
                if (username != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, null);  // Add authorities if needed
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Handle token validation failure (e.g., expired, malformed token)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response); // Continue with the filter chain
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extract token from the "Bearer <token>" format
        }
        return null;
    }
}
