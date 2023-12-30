package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateUserDetailsRequest {

	private String userPersonalNo;

	private String firstName;

	private String lastName;

	private String password;

	private String email;

	private String gender;

	private String middleName;
}
