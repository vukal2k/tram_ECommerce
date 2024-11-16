package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CreateUserRequest {

	@JsonProperty
	private String username;

	@NotEmpty(message = "Password cannot be empty")
	@Size(min = 7, message = "Password must be at least 7 characters long")
	@JsonProperty
	private String password;

	@JsonProperty
	private String passwordConfirmation;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}
}
