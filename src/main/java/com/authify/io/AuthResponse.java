package com.authify.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class AuthResponse {

	private String email;
	private String token;
}
