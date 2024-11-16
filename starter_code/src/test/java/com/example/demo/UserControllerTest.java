package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginRequest;
import com.example.demo.security.JwtService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setCart(new Cart());
    }

    @Test
    public void testCreateUser_Success() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password123");
        createUserRequest.setPasswordConfirmation("password123");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(user.getCart());

        // When
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        // Given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password123");
        createUserRequest.setPasswordConfirmation("mismatch123");

        // When
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Password field does not match confirm password field", response.getBody());
    }

    @Test
    public void testFindById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        // When
        ResponseEntity<User> response = userController.findById(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testFindById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // When
        ResponseEntity<User> response = userController.findById(1L);

        // Then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUserName_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        // When
        ResponseEntity<User> response = userController.findByUserName("testuser");

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testFindByUserName_NotFound() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        // When
        ResponseEntity<User> response = userController.findByUserName("testuser");

        // Then
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testLogin_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(jwtService.generateToken("testuser")).thenReturn("jwt_token");

        // When
        ResponseEntity<String> response = userController.login(loginRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt_token", response.getBody());
    }

    @Test
    public void testLogin_InvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        // When & Then
        try {
            userController.login(loginRequest);
            fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            assertEquals("Invalid credentials", e.getMessage());
        }
    }
}
