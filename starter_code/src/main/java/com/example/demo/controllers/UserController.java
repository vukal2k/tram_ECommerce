package com.example.demo.controllers;

import com.example.demo.model.requests.LoginRequest;
import com.example.demo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final  JwtService jwtService;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

    public UserController(com.example.demo.security.JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		if (!createUserRequest.getPassword().equals(createUserRequest.getPasswordConfirmation())){
			return ResponseEntity.badRequest().body("Password field does not match confirm password field");
		}

		user.setPassword(createUserRequest.getPassword());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
		// Find user by username
		User user = userRepository.findByUsername(loginRequest.getUsername());

		// Check if the password matches
		if (!loginRequest.getPassword().equals(user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		// Generate JWT Token
		return ResponseEntity.ok(jwtService.generateToken(user.getUsername()));
	}

}
