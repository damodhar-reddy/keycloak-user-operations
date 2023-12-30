package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

	private String username;

	private boolean enabled;

	private String email;

	private String firstName;

	private String lastName;
	
	private CredentialDTO[] credentials;
}
