package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateUserRequest {

	@JsonProperty
	private String username;
    @JsonProperty
	private String password;
    @JsonProperty
	private String confirmedPassword;

	public CreateUserRequest() {
	}

	public CreateUserRequest(String username, String password, String confirmedPassword) {
		this.username = username;
		this.password = password;
		this.confirmedPassword = confirmedPassword;
	}

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

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}


}
