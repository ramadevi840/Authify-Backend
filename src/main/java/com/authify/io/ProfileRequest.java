package com.authify.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
  
	
	@NotBlank(message="Name Should not be empty")
	private String name;
	@Email(message="entervalid Email address")
	@NotNull(message="email should not be empty")
	private String email;
	@Size(min=6, message="password must be atleast 6 characters")
	 private String password; 
	
	
	
}
