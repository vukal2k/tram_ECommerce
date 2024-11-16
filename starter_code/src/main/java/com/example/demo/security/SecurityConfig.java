package com.example.demo.security;

import com.example.demo.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final  JwtService jwtService;

    public SecurityConfig(com.example.demo.security.JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, jwtService);  // Pass jwtUtil to JwtAuthenticationFilter
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter(null), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/user/create").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable(); ; // Require authentication for other endpoints
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();  // This provides the AuthenticationManager
    }
}
